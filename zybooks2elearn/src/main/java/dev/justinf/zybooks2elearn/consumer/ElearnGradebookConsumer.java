package dev.justinf.zybooks2elearn.consumer;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.justinf.zybooks2elearn.Z2EApp;
import dev.justinf.zybooks2elearn.data.ElearnBean;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class ElearnGradebookConsumer extends SheetConsumer {

    private final Z2EApp app;

    public ElearnGradebookConsumer(Z2EApp app) {
        this.app = app;
    }

    @Override
    public boolean consume(String filePath) {
        try {
            boolean formatCheck = true;
            Scanner sc = new Scanner(new File(filePath));
            if (sc.hasNextLine()) {
                String firstLine = sc.nextLine();
                if (!firstLine.startsWith("Student")) {
                    // This is not the correct file
                    app.log("Error: Invalid eLearn Gradebook format.");
                    app.log("Please verify that this file is an eLearn Gradebook.");
                    formatCheck = false;
                }
            } else {
                // This is not the correct file
                app.log("Error: Invalid eLearn Gradebook format.");
                app.log("Please verify that this file is an eLearn Gradebook.");
                formatCheck = false;
            }
            sc.close();
            if (!formatCheck) return false;

            List<ElearnBean> beans = new CsvToBeanBuilder(new FileReader(filePath))
                    .withType(ElearnBean.class)
                    .build()
                    .parse();

            app.getGradebook().importFromConsumer(beans);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}