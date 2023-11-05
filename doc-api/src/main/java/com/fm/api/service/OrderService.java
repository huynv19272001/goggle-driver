package com.fm.api.service;


import com.fm.api.error.ErrorMessage;
import com.fm.api.payload.response.OrderActionResponse;
import com.fm.api.utils.ContractNameOrder;
import com.fm.api.utils.FileUtil;
import com.fm.api.utils.ListResult;
import com.fm.api.utils.WriteDataToOrderCSV;
import com.fm.base.models.dto.*;
import com.fm.base.models.sql.*;
import com.fm.base.repository.sql.*;
import com.fm.base.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fm.api.error.ErrorMessage.FILE_OPTION_MUST_BE_PDF;
import static com.fm.api.error.ErrorMessage.FILE_UPLOAD_NO_SUCCESSFUL_25m;
import static com.fm.api.utils.Constants.FILE_EXCEPT;
import static com.fm.api.utils.Constants.FILE_SIZE;
import static com.fm.base.models.sql.Order.Status.*;
import static com.fm.base.models.sql.PriceList.Unit.SET;
import static com.fm.base.models.sql.PriceList.Unit.SHEET;
import static com.fm.base.models.sql.User.Role.ADMIN;
import static com.fm.base.models.sql.User.Role.USER;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderDAO;

    private ProjectRepository projectDAO;

    private UserRepository userDAO;

    private FileService fileService;

    private FileRepository fileDAO;

    private OrderActionRepository orderActionDAO;

    private ActionHistoryRepository actionHistoryDAO;

    private PriceListRepository priceListDAO;


    public List<Order> getAllOrder(String sortBy) {
        return orderDAO.findAll(Sort.by(sortBy).descending());
    }

    public ReturnFileUrl createOrder(List<MultipartFile> multipartFiles, Integer priceOrder, Integer projectId, Integer priceListId, String orderCode, String note) {
        if (multipartFiles == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_IS_NOT_TO_BLANK);
        }
        Integer userLogin = UserDetail.getAuthorizedUser().getId();
        List<ReturnFileUrl.saveFileOrder> list = new ArrayList<>();
        checkProjectId(projectId);
        Order newOrder = new Order();
        if (Objects.equals(USER.name(), userDAO.getById(userLogin).getRole().name())) {
            projectDAO.findByProjectIdToUserAndDeletedAtNull(projectId, userLogin).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_PROJECT_ID));
            newOrder.setIsApproved(false);
        } else {
            projectDAO.findByProjectIdToUserAndDeletedAtNullAdmin(projectId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_PROJECT_ID_ADMIN));
            newOrder.setIsApproved(true);
            newOrder.setReceiveTime(new DateTime());
        }
        int totalFilePdf = 0, roundingPrice;
        checkFile(multipartFiles);
        totalFilePdf = getFilePageNumberUp(multipartFiles, totalFilePdf);
        if (priceListId != null) {
            Optional<PriceList> findPrice = Optional.ofNullable(priceListDAO.findByIdAndDeletedAtNull(priceListId, projectId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_DO_NOT_IN_PROJECT_ID)));
            if (findPrice.isPresent()) {
                DateTime dateTime = DateTime.now();
                if (totalFilePdf % 2 == 0) {
                    roundingPrice = totalFilePdf;
                } else {
                    roundingPrice = totalFilePdf + 1;
                }
                if (findPrice.get().getUnit() == SHEET && !dateTime.isAfter(findPrice.get().getEndTime()) && !dateTime.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                    newOrder.setPrice(roundingPrice / 2 * findPrice.get().getPrice());
                }
                if (findPrice.get().getUnit() == SET && !dateTime.isAfter(findPrice.get().getEndTime()) && !dateTime.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                    newOrder.setPrice(findPrice.get().getPrice());
                }
                if ((findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && !dateTime.isAfter(findPrice.get().getEndTime()) && !dateTime.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() == null) {
                    if (priceOrder == null || priceOrder <= 0) {
                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_PRICE_ORDER);
                    }
                    newOrder.setPrice(Double.valueOf(priceOrder));
                }
                if ((findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && (dateTime.isAfter(findPrice.get().getEndTime()) || dateTime.isBefore(findPrice.get().getStartTime()))) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_EXPIRE);
                }
                if (findPrice.get().getUnit() == null) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_VALUE_UNIT_NO_NULL);
                }
            }
        } else throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_CAN_NOT_LEFT_EMPTY);

        if (StringUtils.isNotBlank(orderCode) && orderCode.length() <= 255) {
            if (orderDAO.findByOrderCode(orderCode).isPresent()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_CODE_ALREADY_EXIST);
            }
            if (orderCode.matches(ContractNameOrder.orderCode)) {
                newOrder.setOrderCode(orderCode);
            } else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_CODE_MUST_ENTER_THE_CORRECT_FORMAT);
            }
        } else
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_CODE_CANT_NOT_BE_EMPTY_AND_MUST_ENTER_ALL_EIGHT_CHARACTERS);
        if (StringUtils.isNotBlank(note)) {
            newOrder.setNote(note);
        }
        Optional<User> user = userDAO.findUserById(projectDAO.getById(projectId).getUserId());
        user.ifPresent(user1 -> newOrder.setCustomerName(user1.getUserName()));
        newOrder.setProjectName(projectDAO.getById(projectId).getName());
        newOrder.setPriceListId(priceListId);
        newOrder.setProjectId(projectId);
        newOrder.setStatus(PENDING);
        newOrder.setNumberReprint(0);
        newOrder.setPageTotal(totalFilePdf);
        newOrder.setCreatorId(userLogin);
        Order uploadOrder = orderDAO.save(newOrder);
        fileService.uploadFilePdf(multipartFiles, uploadOrder.getId(), list);
        ReturnFileUrl returnFileUrl = new ReturnFileUrl();
        returnFileUrl.setOrder(uploadOrder);
        returnFileUrl.setFileNewPush(list);

        return returnFileUrl;

    }

    public Object editOrder(List<MultipartFile> multipartFiles, Integer orderId, List<Integer> fileIds, Integer projectId, Integer priceListId, Integer priceOrder, String note) {
        List<ReturnFileUrl.saveFileOrder> list = new ArrayList<>();
        checkProjectId(projectId);
        projectDAO.findByIdAndDeletedAtNull(projectId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_PROJECT_ID));
        Optional<Order> findOrderId = Optional.ofNullable(orderDAO.findByIdAndDeletedAtNull(orderId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID)));
        if (findOrderId.isPresent()) {
            if (RECEIVED.equals(findOrderId.get().getStatus()) || PENDING.equals(findOrderId.get().getStatus())) {
                int pageTotalNew = 0, roundingPrice;
                DateTime newDate = DateTime.now();
                if (multipartFiles != null) {
                    checkFile(multipartFiles);
                    pageTotalNew = getPageTotal(multipartFiles, orderId);
                    List<FileAttachment> fileAttachment = fileDAO.findByOrderId(orderId);
                    Optional<PriceList> findPrice = Optional.ofNullable(priceListDAO.findByIdAndDeletedAtNull(priceListId, projectId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_DO_NOT_IN_PROJECT_ID)));
                    if (fileIds != null) {
                        fileDAO.findByIdAndDeletedAtNull(fileIds, orderId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_DO_NOT_IN_ORDER));
                        fileService.deleteFile(fileIds);
                        List<FileAttachment> listFileOrderId1 = fileDAO.findByOrderId(orderId);
                        pageTotalNew = getPageTotal(multipartFiles, orderId);
                        if (pageTotalNew % 2 == 0) {
                            roundingPrice = pageTotalNew;
                        } else {
                            roundingPrice = pageTotalNew + 1;
                        }
                        Order order = findOrderId.orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID));
                        order.setPageTotal(pageTotalNew);
                        if (StringUtils.isNotBlank(note)) {
                            order.setNote(note);
                        }
                        if (priceListId != null) {
                            if (findPrice.isPresent()) {
                                if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SHEET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                                    order.setPrice(roundingPrice / 2 * findPrice.get().getPrice());
                                }
                                if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                                    order.setPrice(findPrice.get().getPrice());
                                }
                                if ((findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && !newDate.isAfter(findPrice.get().getStartTime()) && !newDate.isBefore(findPrice.get().getEndTime()) && findPrice.get().getPrice() == null) {
                                    if (priceOrder == null || priceOrder <= 0) {
                                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_PRICE_ORDER);
                                    }
                                    order.setPrice(Double.valueOf(priceOrder));
                                }
                                if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && (findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && (newDate.isAfter(findPrice.get().getEndTime()) || newDate.isBefore(findPrice.get().getStartTime()))) {
                                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_EXPIRE);
                                }
                            }
                        }
                        return getReturnFileUrl(multipartFiles, orderId, list, listFileOrderId1, order);
                    }
                    Order order = findOrderId.orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_DOES_NOT_EXISTS));
                    order.setPageTotal(pageTotalNew);
                    if (pageTotalNew % 2 == 0) {
                        roundingPrice = pageTotalNew;
                    } else {
                        roundingPrice = pageTotalNew + 1;
                    }
                    if (StringUtils.isNotBlank(note)) {
                        order.setNote(note);
                    }
                    if (findPrice.isPresent()) {
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SHEET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                            order.setPrice(roundingPrice / 2 * findPrice.get().getPrice());
                        }
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                            order.setPrice(findPrice.get().getPrice());
                        }
                        if ((findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && !newDate.isAfter(findPrice.get().getStartTime()) && !newDate.isBefore(findPrice.get().getEndTime()) && findPrice.get().getPrice() == null) {
                            if (priceOrder == null || priceOrder <= 0) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_PRICE_ORDER);
                            }
                            order.setPrice(Double.valueOf(priceOrder));
                        }
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && (findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && (newDate.isAfter(findPrice.get().getEndTime()) || newDate.isBefore(findPrice.get().getStartTime()))) {
                            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_EXPIRE);
                        }
                    }
                    return getReturnFileUrl(multipartFiles, orderId, list, fileAttachment, order);
                } else {
                    if (fileIds == null) {
                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_ID_DO_NOT_EXIST);
                    }
                    fileDAO.findByIdAndDeletedAtNull(fileIds, orderId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_DO_NOT_IN_ORDER));
                    List<FileAttachment> listFileOrderId1 = fileDAO.findByOrderId(orderId);
                    if (listFileOrderId1.size() <= fileIds.size()) {
                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NO_DELETE_ALL_FILE);
                    }
                    fileService.deleteFile(fileIds);
                    List<FileAttachment> listFileOrderId = fileDAO.findByOrderId(orderId);
                    for (FileAttachment fileAttachment : listFileOrderId) {
                        pageTotalNew += fileAttachment.getPageTotal();
                    }
                    if (pageTotalNew % 2 == 0) {
                        roundingPrice = pageTotalNew;
                    } else {
                        roundingPrice = pageTotalNew + 1;
                    }
                    Order order = findOrderId.orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID));
                    order.setPageTotal(pageTotalNew);
                    if (StringUtils.isNotBlank(note)) {
                        order.setNote(note);
                    }
                    Optional<PriceList> findPrice = Optional.ofNullable(priceListDAO.findByIdAndDeletedAtNull(priceListId, projectId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_DO_NOT_IN_PROJECT_ID)));
                    if (findPrice.isPresent()) {
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SHEET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                            order.setPrice(roundingPrice / 2 * findPrice.get().getPrice());
                        }
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && findPrice.get().getUnit() == SET && !newDate.isAfter(findPrice.get().getEndTime()) && !newDate.isBefore(findPrice.get().getStartTime()) && findPrice.get().getPrice() != null) {
                            order.setPrice(findPrice.get().getPrice());
                        }
                        if ((findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && !newDate.isAfter(findPrice.get().getStartTime()) && !newDate.isBefore(findPrice.get().getEndTime()) && findPrice.get().getPrice() == null) {
                            if (priceOrder == null || priceOrder <= 0) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_PRICE_ORDER);
                            }
                            order.setPrice(Double.valueOf(priceOrder));
                        }
                        if (projectId.equals(priceListDAO.getById(priceListId).getProjectId()) && (findPrice.get().getUnit() == SHEET || findPrice.get().getUnit() == SET) && (newDate.isAfter(findPrice.get().getEndTime()) || newDate.isBefore(findPrice.get().getStartTime()))) {
                            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_ID_EXPIRE);
                        }
                    }
                    Order order1 = orderDAO.save(order);
                    ReturnFileUrl returnFileUrl = new ReturnFileUrl();
                    returnFileUrl.setOrder(order1);
                    returnFileUrl.setFileAttachments(listFileOrderId);
                    return returnFileUrl;
                }
            } else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, ErrorMessage.PLEASE_SEE_AGAIN);
            }
        }
        return null;
    }

    private @NotNull ReturnFileUrl getReturnFileUrl(List<MultipartFile> multipartFiles, Integer orderId, List<ReturnFileUrl.saveFileOrder> list, List<FileAttachment> fileAttachment, Order order) {
        fileService.uploadFilePdf(multipartFiles, orderId, list);
        Order editOrder = orderDAO.save(order);
        ReturnFileUrl returnFileUrl = new ReturnFileUrl();
        returnFileUrl.setOrder(editOrder);
        returnFileUrl.setFileNewPush(list);
        returnFileUrl.setFileAttachments(fileAttachment);
        return returnFileUrl;
    }

    private void checkProjectId(Integer projectId) {
        if (projectId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_ID_IS_NOT_TO_BLANK);
        }
    }

    private void checkFile(List<MultipartFile> multipartFiles) {

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = fileService.getFileName(multipartFile);
            String checkFileName = FileUtil.getSafeFileName(fileName);
            String extFile = FilenameUtils.getExtension(checkFileName);
            if (multipartFile.getSize() > FILE_SIZE) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, FILE_UPLOAD_NO_SUCCESSFUL_25m);
            }
            if (!FILE_EXCEPT.contains(extFile)) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, FILE_OPTION_MUST_BE_PDF);
            }
        }
    }


    private int getPageTotal(List<MultipartFile> multipartFiles, Integer orderId) {
        int filePageNumberUp = 0, pageNumberOfAgain = 0, editPageTotal;
        filePageNumberUp = getFilePageNumberUp(multipartFiles, filePageNumberUp);
        List<FileAttachment> listFileOrderId = fileDAO.findByOrderId(orderId);
        for (FileAttachment fileAttachment : listFileOrderId) {
            pageNumberOfAgain += fileAttachment.getPageTotal();
        }
        editPageTotal = filePageNumberUp + pageNumberOfAgain;
        return editPageTotal;
    }

    private int getFilePageNumberUp(List<MultipartFile> multipartFiles, int filePageNumberUp) {
        for (MultipartFile multipartFile : multipartFiles) {
            InputStream inputStream;
            try {
                inputStream = multipartFile.getInputStream();
                PDDocument document = PDDocument.load(inputStream);
                filePageNumberUp += document.getNumberOfPages();

            } catch (IOException e) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_COUNT_THE_NUMBER_OF_PAGE);
            }
        }
        return filePageNumberUp;
    }

    public OrderDTO editPriceOrder(UpdateOrderDTO updateOrderDTO) {
        List<Order> orders = orderDAO.findByListId(updateOrderDTO.getOrderIds());
        if (orders.size() == updateOrderDTO.getOrderIds().size()) {
            List<Order> orderList = orders.stream().peek(order -> {
                if (RECEIVED == order.getStatus() || PENDING == order.getStatus()) {
                    if (updateOrderDTO.getPrice() == null || updateOrderDTO.getPrice() < 0) {
                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_BIGGER_0);
                    }
                    order.setPrice(updateOrderDTO.getPrice());
                } else {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, ErrorMessage.PLEASE_SEE_AGAINS);
                }
            }).collect(Collectors.toList());
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderList(orderDAO.saveAll(orderList));
            return orderDTO;
        }

        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID);
    }

    public OrderDTO editNoteOrder(UpdateOrderDTO updateOrderDTO) {
        List<Order> orders = orderDAO.findByListId(updateOrderDTO.getOrderIds());
        if (orders.size() == updateOrderDTO.getOrderIds().size()) {
            List<Order> orderList = orders.stream().peek(order -> {
                if (RECEIVED == order.getStatus() || PENDING == order.getStatus()) {
                                if (StringUtils.isNotBlank(updateOrderDTO.getNote())) {
                        order.setNote(updateOrderDTO.getNote());
                    } else {
                        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_NOTE);
                    }

                } else {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, ErrorMessage.PLEASE_SEE_AGAIN);
                }
            }).collect(Collectors.toList());
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderList(orderDAO.saveAll(orderList));
            return orderDTO;
        }

        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID);
    }

    public OrderDTO updatePriceNoteStatus(Integer orderId, UpdatePriceNoteOrderDTO updatePriceNoteOrder) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        Order order = optionalOrder.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_DOES_NOT_EXISTS));

        if (!(RECEIVED == order.getStatus() || PENDING == order.getStatus())) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, ErrorMessage.PLEASE_SEE_AGAIN);
        }
        if (StringUtils.isNotBlank(updatePriceNoteOrder.getNote())) {
            order.setNote(updatePriceNoteOrder.getNote());
        }
        if (updatePriceNoteOrder.getPrice() != null) {
            if (updatePriceNoteOrder.getPrice() == null || updatePriceNoteOrder.getPrice() < 0) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_BIGGER_0);
            }
            order.setPrice(updatePriceNoteOrder.getPrice());
        }
        Order savedOrder = orderDAO.save(order);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrder(savedOrder);
        return orderDTO;
    }


    public OrderDTO updateOrder(Integer id, String customerName) {
        Integer creatorId = UserDetail.getAuthorizedUser().getId();
        if (Objects.equals(ADMIN.name(), userDAO.getById(creatorId).getRole().name())) {
            Optional<Order> optionalOrder = Optional.ofNullable(orderDAO.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_ORDER_ID)));
            Order order = optionalOrder.orElseThrow();
            order.setCustomerName(customerName);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrder(orderDAO.save(order));
            return orderDTO;
        } else
            return null;
    }

    public OrderDTO getOrder(Integer orderId) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_DOES_NOT_EXISTS);
        }
        Order order = optionalOrder.get();
        Map<Integer, String> filesAttachment = fileDAO.findByOrderId(orderId).stream().collect(Collectors.toMap(FileAttachment::getId, FileAttachment::getFileUrl));

        return new OrderDTO().withOrder(order).withFilesAttack(filesAttachment);
    }

    public ListResult<Order> getOrdersByUser(int page, int size, String orderBy, boolean desc) {
        Integer userId = UserDetail.getAuthorizedUser().getId();
        User user = userDAO.getById(userId);
        if (user.getRole().name().equals(USER.name())) {
            return ListResult.from(orderDAO.listOrderByUserLogin(user.getId(), PageableUtils.pageable(page, size, orderBy, desc)));
        } else return ListResult.from(orderDAO.findAll(PageableUtils.pageable(page, size, orderBy, desc)));
    }

    public Order cancelOrder(Integer orderId) {
        Integer userId = UserDetail.getAuthorizedUser().getId();
        boolean isAdmin = userDAO.getById(userId).getRole().equals(ADMIN);
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        optionalOrder.ifPresentOrElse(order -> {
            if (isAdmin || order.getCreatorId().equals(userId)) {
                if (order.getStatus().equals(PRINTED) || order.getStatus().equals(PACKED) || order.getStatus().equals(DELIVERED)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_CANCEL_ORDER);
                }
                order.setStatus(CANCEL);
                orderDAO.save(order);
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorMessage.HAVE_NOT_PERMISSION);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_DOES_NOT_EXISTS);
        });
        return orderDAO.getById(orderId);
    }

    public Order approveOrder(Integer orderId) {
        Optional<Order> existsOrder = orderDAO.findById(orderId);
        existsOrder.ifPresentOrElse(order -> {
            if (!order.getStatus().equals(PENDING)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.CAN_NOT_APPROVE_ORDER);
            }
            order.setIsApproved(true);
            order.setStatus(RECEIVED);
            order.setReceiveTime(new DateTime());
            orderDAO.save(order);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_DOES_NOT_EXISTS);
        });
        return orderDAO.getById(orderId);
    }

    public ListResult<Order> listOrderWithFindCondition(String customerName, String orderCode, String status, String fromDate, String toDate, int page, int size, String orderBy, boolean desc) {
        if (StringUtils.isNotBlank(orderCode)) {
            orderCode = orderCode.replaceAll("\\s+", " ").trim();
        }
        return ListResult.from(orderDAO.listOrderWithFindCondition(customerName, orderCode, status, fromDate, toDate, PageableUtils.pageable(page, size, orderBy, desc)));
    }

    public OrderActionResponse updateStatus(Integer orderId, OrderAction.Type typeAction) {
        Optional<Order> order = orderDAO.findByIdAndDeletedAtNull(orderId);
        Integer userId = UserDetail.getAuthorizedUser().getId();
        LocalDateTime timeNow = LocalDateTime.now();

        if (order.isPresent()) {
            Order.Status orderStatus = order.get().getStatus();
            Integer pageTotal = order.get().getPageTotal();
            if (OrderAction.Type.DELIVERED.equals(typeAction)) {
                if (Order.Status.PACKED.equals(orderStatus)) {
                    orderDAO.updateStatusByIdAndTransferTime(orderId, String.valueOf(Order.Status.DELIVERED));
                    return new OrderActionResponse().withOrderId(orderId).withNameAction(typeAction.toString()).withTimePacking(0.0).withTimePrint(0.0);
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.createMessageUpdateOrderStatus(orderStatus, typeAction));
            }

            if (order.get().getPriceListId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.PRICE_LIST_OF_ORDER_IS_NOT_NULL);

            Optional<OrderAction> orderActionResponse = orderActionDAO.findByOrderId(orderId);
            if (orderActionResponse.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.createMessageUpdateOrderStatus(orderActionResponse.get().getType().toString()));

            Optional<PriceList> priceList = priceListDAO.findByIdDeletedAtNull(order.get().getPriceListId());
            if (priceList.isPresent()) {
                Double timeHandlePrint = priceList.get().getTimePrint();
                if (SHEET.equals(priceList.get().getUnit()) && pageTotal != null)
                    timeHandlePrint = priceList.get().getTimePrint() * ((int) Math.ceil(pageTotal / 2.0));

                if ((OrderAction.Type.PRINT.equals(typeAction) && RECEIVED.equals(orderStatus))) {
                    orderDAO.updateStatusById(orderId, String.valueOf(PRINTING));
                    //
                    LocalDateTime timePrinted = timeNow.plusSeconds(timeHandlePrint.longValue());
                    orderDAO.updatePrintedTimeById(orderId, timePrinted);

                    OrderAction orderAction = new OrderAction().setOrderId(orderId)
                            .setTimeStart(timeNow)
                            .setTimeEnd(timePrinted)
                            .setType(OrderAction.Type.PRINT)
                            .setUserId(userId);
                    orderActionDAO.save(orderAction);
                    return new OrderActionResponse().withOrderId(orderId).withNameAction(typeAction.toString()).withTimePacking(0.0).withTimePrint(timeHandlePrint);
                }

                if (OrderAction.Type.REPRINT.equals(typeAction) && (Order.Status.PRINTED.equals(orderStatus) || Order.Status.PACKED.equals(orderStatus) || Order.Status.DELIVERED.equals(orderStatus))) {
                    orderDAO.updateStatusById(orderId, String.valueOf(PRINTING));
                    orderDAO.updateNumberReprint(orderId);
                    //
                    LocalDateTime timePrinted = timeNow.plusSeconds(timeHandlePrint.longValue());
                    orderDAO.updatePrintedTimeById(orderId, timePrinted);

                    OrderAction orderAction = new OrderAction().setOrderId(orderId)
                            .setTimeStart(timeNow)
                            .setTimeEnd(timePrinted)
                            .setType(OrderAction.Type.PRINT)
                            .setUserId(userId);
                    orderActionDAO.save(orderAction);

                    ActionHistory actionHistory = new ActionHistory().setOrderId(orderId)
                            .setTimeStart(timeNow)
                            .setTimeEnd(timePrinted)
                            .setType(OrderAction.Type.REPRINT)
                            .setUserId(userId);
                    actionHistoryDAO.save(actionHistory);
                    return new OrderActionResponse().withOrderId(orderId).withNameAction(typeAction.toString()).withTimePacking(0.0).withTimePrint(timeHandlePrint);
                }

                if (OrderAction.Type.PACKING.equals(typeAction) && Order.Status.PRINTED.equals(orderStatus)) {
                    orderDAO.updateStatusById(orderId, String.valueOf(PACKING));
                    Double timeHandlePacking = priceList.get().getTimePacking();
                    if (SHEET.equals(priceList.get().getUnit()) && pageTotal != null)
                        timeHandlePacking = priceList.get().getTimePacking() * ((int) Math.ceil(pageTotal / 2.0));
                    //
                    LocalDateTime timePacked = timeNow.plusSeconds(timeHandlePacking.longValue());
                    orderDAO.updatePackedTimeById(orderId, timePacked);
                    OrderAction orderAction = new OrderAction().setOrderId(orderId)
                            .setTimeStart(timeNow)
                            .setTimeEnd(timePacked)
                            .setType(OrderAction.Type.PACKING)
                            .setUserId(userId);
                    orderActionDAO.save(orderAction);
                    return new OrderActionResponse().withOrderId(orderId).withNameAction(typeAction.toString()).withTimePacking(timeHandlePacking).withTimePrint(0.0);
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.createMessageUpdateOrderStatus(orderStatus, typeAction));
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_FOUND_PRICE_LIST_BY_ID);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_ID_NOT_EXITS);
    }

    public boolean updateStatusOfListOrder(UpdateStatusListOrderDTO request) {
        List<Integer> orderIds = request.getOrderIds();
        Order.Status status = request.getStatus();
        if (!PRINTING.equals(status) && !RECEIVED.equals(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.STATUS_RECEIVED_OR_PRINTING);
        }
        if (!orderIds.isEmpty()) {
            for (Integer orderId : orderIds) {
                if (orderId != null) {
                    Optional<Order> order = orderDAO.findByIdAndDeletedAtNull(orderId);
                    if (order.isPresent()) {
                        Order.Status orderStatus = order.get().getStatus();
                        Integer pageTotal = order.get().getPageTotal();
                        Integer userId = UserDetail.getAuthorizedUser().getId();

                        if (RECEIVED.equals(status) && PENDING.equals(orderStatus)) {
                            orderDAO.updateStatusById(orderId, String.valueOf(RECEIVED));
                        }

                        if (PRINTING.equals(status) && RECEIVED.equals(orderStatus)) {
                            orderDAO.updateStatusById(orderId, String.valueOf(PRINTING));
                            LocalDateTime timeNow = LocalDateTime.now();

                            Optional<PriceList> priceList = priceListDAO.findByIdDeletedAtNull(order.get().getPriceListId());
                            if (priceList.isPresent()) {
                                Double timeHandlePrint = priceList.get().getTimePrint();
                                if (SHEET.equals(priceList.get().getUnit()) && pageTotal != null)
                                    timeHandlePrint = priceList.get().getTimePrint() * ((int) Math.ceil(pageTotal / 2.0));

                                LocalDateTime timePrinted = timeNow.plusSeconds(timeHandlePrint.longValue());
                                orderDAO.updatePrintedTimeById(orderId, timePrinted);

                                OrderAction orderAction = new OrderAction().setOrderId(orderId)
                                        .setTimeStart(timeNow)
                                        .setTimeEnd(timePrinted)
                                        .setType(OrderAction.Type.PRINT)
                                        .setUserId(userId);
                                orderActionDAO.save(orderAction);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public ByteArrayInputStream load() {
        List<Order> orders = orderDAO.findAll();
        return WriteDataToOrderCSV.tutorialsToCSV(orders);
    }
}

