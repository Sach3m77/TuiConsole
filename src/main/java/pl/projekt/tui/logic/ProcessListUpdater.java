package pl.projekt.tui.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessListUpdater {
    public static List<List<String>> getProcessData() {
        List<List<String>> processData = new ArrayList<>();
        processData.add(List.of("ID Procesu", "Komenda", "Zużycie CPU"));

        // Pętla aktualizacji procesów
        ProcessHandle.allProcesses().forEach(process -> {
            ProcessHandle.Info info = process.info();
            String command = info.command().orElse(null);
            java.time.Duration cpuDuration = info.totalCpuDuration().orElse(null);

            // Dodaj informacje do listy tylko jeśli są dostępne
            if (command != null && cpuDuration != null) {
                List<String> rowData = new ArrayList<>();
                rowData.add(Long.toString(process.pid()));
                rowData.add(command);
                rowData.add(Long.toString(cpuDuration.toMillis()) + " ms");
                processData.add(rowData);
            }
        });

        return processData;
    }

    // Metoda do pobierania informacji o procesorze
    public static List<Object> getSystemInfo() {
        List<Object> systemInfo = new ArrayList<>();

        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("windows")) {
                Process process = Runtime.getRuntime().exec("wmic cpu get NumberOfCores");
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                int numberOfCores = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        numberOfCores += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(numberOfCores);

                process = Runtime.getRuntime().exec("wmic CPU get Name");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                String nameProcessor = "";
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() ) {
                        nameProcessor = line.trim();
                    }
                }
                systemInfo.add(nameProcessor);

                process = Runtime.getRuntime().exec("wmic CPU get MaxClockSpeed");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int maxClockSpeed = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        maxClockSpeed += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(maxClockSpeed);


                process = Runtime.getRuntime().exec("wmic CPU get L2CacheSize");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int l2CacheSize = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        l2CacheSize += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(l2CacheSize);



                process = Runtime.getRuntime().exec("wmic CPU get L3CacheSize");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int l3CacheSize = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        l3CacheSize += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(l3CacheSize);

                process = Runtime.getRuntime().exec("wmic CPU get LoadPercentage");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int loadPercentage = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        loadPercentage += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(loadPercentage);

                // Pobierz informacje o pamięci wirtualnej
                process = Runtime.getRuntime().exec("wmic OS get FreeVirtualMemory");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int freeVirtualMemory = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        freeVirtualMemory += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(freeVirtualMemory);

                process = Runtime.getRuntime().exec("wmic OS get FreePhysicalMemory");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int freePhysicalMemory = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        freePhysicalMemory += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(freePhysicalMemory);

                process = Runtime.getRuntime().exec("wmic OS get TotalVisibleMemorySize");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                int totalVisibleMemorySize = 0;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        totalVisibleMemorySize += Integer.parseInt(line.trim());
                    }
                }
                systemInfo.add(totalVisibleMemorySize);

                process = Runtime.getRuntime().exec("wmic PATH Win32_VideoController GET Name");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                line="";
                String nameGPU = "";
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() ) {
                        nameGPU = line.trim();
                    }
                }
                systemInfo.add(nameGPU);


            } else if (osName.contains("linux")) {
                // Dodaj obsługę systemu Linux
            } else {
                int availableProcessors = Runtime.getRuntime().availableProcessors();
                systemInfo.add(availableProcessors);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemInfo;
    }

}

