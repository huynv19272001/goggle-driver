package com.fm.base.models.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@With
@Entity
@Table(name = "blacklist_token")
public class BlacklistToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "access_token")
    private String accessToken;

    @CreatedDate
    private DateTime createdAt;
}
