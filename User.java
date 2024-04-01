package com.projectgroup.project.Model;


import javax.persistence.*;
import lombok.*;
import org.apache.ibatis.annotations.Options;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequence-generator"
    )
    @SequenceGenerator(
            name = "sequence-generator",
            sequenceName = "user_id"
    )
    private int id;

    private String username = "";

    private String password = "";

    private String head = "";// 头像
    private String post_collecting = "";

    private String good_collecting = "";

    private int privilege;

    private String post_history = "";

    private String good_history = "";

    private String organization = "";


}
