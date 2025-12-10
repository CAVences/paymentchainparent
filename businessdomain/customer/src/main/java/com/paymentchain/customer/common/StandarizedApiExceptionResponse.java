package com.paymentchain.customer.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "The standarized error response")
@NoArgsConstructor
@Data
public class StandarizedApiExceptionResponse {

    @Schema(description = "The unique uri indentifier that categories the error", name = "type", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/errors/invalid-request" )
    private String type;

    @Schema(description = "A brief, human.readble message about the error", name = "title", requiredMode = Schema.RequiredMode.REQUIRED, example = "The user does not have autorization" )
    private String title;

    @Schema(description = "The unique error code", name = "code", requiredMode = Schema.RequiredMode.REQUIRED, example = "192" )
    private String code;

    @Schema(description = "A human-readble explanation of the error", name = "detail", requiredMode = Schema.RequiredMode.REQUIRED, example = "The user does not have the propertly permissions to acces the resource, pleace contact with" )
    private String detail;

    @Schema(description = "A URI that indetifies the specific ocurrence of the error", name = "instance", requiredMode = Schema.RequiredMode.REQUIRED, example = "/errors/authentication/not-authorized/01" )
    private String instance;

    public StandarizedApiExceptionResponse(String type, String title, String code, String detail, String instance) {
        super();
        this.type = type;
        this.title = title;
        this.code = code;
        this.detail = detail;
        this.instance = instance;
    }
}
