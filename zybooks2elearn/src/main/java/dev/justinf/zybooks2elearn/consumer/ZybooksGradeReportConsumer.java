package dev.justinf.zybooks2elearn.consumer;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.justinf.zybooks2elearn.Z2EApp;
import dev.justinf.zybooks2elearn.data.ZybooksBean;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class ZybooksGradeReportConsumer extends SheetConsumer {

    private final Z2EApp app;

    public ZybooksGradeReportConsumer(Z2EApp app) {
        this.app = app;
    }

    @Override
    public boolean consume(String filePath) {
        try {
            boolean formatCheck = true;
            Scanner sc = new Scanner(new File(filePath));
            if (sc.hasNextLine()) {
                String firstLine = sc.nextLine();
                if (!firstLine.startsWith("Last name")) {
                    // This is not the correct file
                    app.log("Error: Invalid zyBooks Report format.");
                    app.log("Please verify that this file is a zyBooks Report.");
                    formatCheck = false;
                }
            } else {
                // This is not the correct file
                app.log("Error: Invalid zyBooks Report format.");
                app.log("Please verify that this file is a zyBooks Report.");
                formatCheck = false;
            }
            sc.close();
            if (!formatCheck) return false;

            List<ZybooksBean> beans = new CsvToBeanBuilder(new FileReader(filePath))
                    .withType(ZybooksBean.class)
                    .build()
                    .parse();

            app.getAssignmentReport().importFromConsumer(beans);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}