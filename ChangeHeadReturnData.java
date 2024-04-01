package com.projectgroup.project.ReturnData;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeHeadReturnData {
    Integer code;
    String message = "";
    String data = "";
}
