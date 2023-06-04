package Setting;

import io.qameta.allure.Step;

import java.io.IOException;

public class SettingProperty {
    private String urlProperty;

    @Step("Получить baseURL")
    public String getPropertyUrl() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        urlProperty = System.getProperty("baseUrl");
        return urlProperty;
    }
}
