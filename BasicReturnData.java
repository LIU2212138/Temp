package com.projectgroup.project.ReturnData;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicReturnData implements Serializable {
    Integer code;
    String message = "";
    Data data;
    public BasicReturnData(int code, String message) {
        this.code = code;
        this.message = message;
        data = new Data();
    }
    @lombok.Data
    @Getter
    @Setter
    private static class Data{
    }
}
