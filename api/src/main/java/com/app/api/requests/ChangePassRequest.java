package com.app.api.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePassRequest {
    private String currentPass;
    private String newPass;
    private String confirmPass;

}
