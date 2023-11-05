package com.fm.base.repository.sql;

import com.fm.base.models.sql.User;
import com.fm.base.repository.sql.custom.UserCustomRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends BaseRepository<User, Integer>, UserCustomRepository {

    @Query(value = "SELECT  *  FROM users p WHERE deleted_at is null", nativeQuery = true)
    List<User> findAllByDeletedAtNull();

    @Query(value = "SELECT  *  FROM users p WHERE id=:id AND deleted_at is null", nativeQuery = true)
    Optional<User> findByIdAndDeletedAtNull(Integer id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhoneNumber(String phoneNumber);
    Optional<User> findByUserName(String userName);

    @Query(value = "SELECT * FROM users  WHERE users.user_name = :userNameOrEmail OR users.email = :userNameOrEmail",nativeQuery = true)
    Optional<User> findByUserNameOrEmail( String userNameOrEmail);

    @Query(value = "SELECT  *  FROM users u WHERE  u.user_name =:userName OR u.email =:email OR u.phone_number =:phoneNumber", nativeQuery = true)
    List<User> findByUserNameOrEmailOrPhoneNumber(String userName, String email, String phoneNumber);

    @Query(value = "SELECT * from users u where u.role = 'USER'",nativeQuery = true)
    List<User> listPartner ();

    List<User> findUserByIdIn(List<Integer> userIds);

    Optional<User> findUserById(Integer userId);
}
