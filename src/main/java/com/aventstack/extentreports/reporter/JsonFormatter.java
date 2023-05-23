package com.aventstack.extentreports.reporter;

import com.aventstack.extentreports.gson.GsonExtentTypeAdapterBuilder;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.observer.ReportObserver;
import com.aventstack.extentreports.observer.entity.ReportEntity;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonFormatter extends AbstractFileReporter implements ReporterConfigurable, ReportObserver<ReportEntity> {

    private static final String FILE_NAME = "extent.json";

    private final Set<Map<Type, TypeAdapter<?>>> typeAdapterMappings = new HashSet<>();

    public JsonFormatter(File file) {
        super(file);
    }

    public JsonFormatter(String filePath) {
        super(new File(filePath));
    }

    public void addTypeAdapterMapping(Map<Type, TypeAdapter<?>> typeAdapterMapping) {
        this.typeAdapterMappings.add(typeAdapterMapping);
    }

    @Override
    public Observer<ReportEntity> getReportObserver() {
        return new Observer<ReportEntity>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ReportEntity value) {
                flush(value);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void flush(ReportEntity value) {
        GsonExtentTypeAdapterBuilder.Builder builder = GsonExtentTypeAdapterBuilder.builder()
                .withBddTypeAdapterFactory();
        for (Map<Type, TypeAdapter<?>> typeAdapterMapping : typeAdapterMappings) {
            for (Type type : typeAdapterMapping.keySet()) {
                builder.registerTypeAdapter(type, typeAdapterMapping.get(type));
            }
        }
        Gson gson = builder.build();
        final String filePath = getFileNameAsExt(FILE_NAME, new String[]{".json"});
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            List<Test> list = value.getReport().getTestList();
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadJSONConfig(File jsonFile) throws IOException {
    }

    @Override
    public void loadJSONConfig(String jsonString) throws IOException {

    }

    @Override
    public void loadXMLConfig(File xmlFile) throws IOException {
    }

    @Override
    public void loadXMLConfig(String xmlFile) throws IOException {
    }
}
