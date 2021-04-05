package Main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LookUpTaskDTO {
    String url;
    String doctorOrCabinetName;
    long userId;
    long chatId;
}
