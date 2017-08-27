package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class LogEntryController {
    private SimpleDateFormat sdf = new SimpleDateFormat();

    @FXML
    private Text timestamp;

    @FXML
    private Text object;

    @FXML
    private Text msg;

    public void set(LogMsg logMsg) {
        timestamp.setText(sdf.format(new Date(logMsg.getTimestamp())));
        msg.setText(logMsg.getMsg());
    }
}
