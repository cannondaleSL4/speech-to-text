package com.dmba.file.service;

import com.dmba.config.SpeechProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FileFinderImpl implements FileFinder {

    private final SpeechProperties speechProperties;
    private final File dir;
    private final long startTime;

    @Autowired
    public FileFinderImpl(SpeechProperties speechProperties) {
        this.speechProperties = speechProperties;
        this.dir = new File(speechProperties.getDirPath());
        this.startTime = System.currentTimeMillis();
    }

    public List<File> getNewFiles() {
        File[] files = dir.listFiles((FileFilter) file ->
                file.isFile() && file.lastModified() > startTime
        );

        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified))
                .collect(Collectors.toList());
    }

}
