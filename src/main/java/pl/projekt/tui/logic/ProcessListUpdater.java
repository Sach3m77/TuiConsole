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
    public static int getAvailableProcessors() {
        String osName = System.getProperty("os.name").toLowerCase();
        int availableProcessors = 0;
        try {
            if (osName.contains("windows")) {
                Process process = Runtime.getRuntime().exec("wmic cpu get NumberOfCores");
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        availableProcessors += Integer.parseInt(line.trim());
                    }
                }

                // Pobierz temperaturę CPU
                process = Runtime.getRuntime().exec("wmic /namespace:\\\\root\\wmi PATH MSAcpi_ThermalZoneTemperature get CurrentTemperature");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        // Przetwórz otrzymane dane o temperaturze CPU
                        // Przykładowo, możesz wyświetlić temperaturę lub zapisać ją do zmiennej
                        System.out.println("Temperatura CPU: " + line.trim());
                    }
                }

                // Pobierz taktowanie procesora
                process = Runtime.getRuntime().exec("wmic cpu get CurrentClockSpeed");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && Character.isDigit(line.trim().charAt(0))) {
                        // Przetwórz otrzymane dane o taktowaniu procesora
                        // Przykładowo, możesz wyświetlić taktowanie procesora lub zapisać je do zmiennej
                        System.out.println("Taktowanie procesora: " + line.trim());
                    }
                }

                // Pobierz zużycie pamięci
                process = Runtime.getRuntime().exec("systeminfo | findstr /C:\"Total Physical Memory\" /C:\"Available Physical Memory\"");
                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        // Przetwórz otrzymane dane o zużyciu pamięci
                        // Przykładowo, możesz wyświetlić informacje o pamięci lub zapisać je do zmiennej
                        System.out.println("Informacje o pamięci: " + line.trim());
                    }
                }

            } else if (osName.contains("linux")) {
                Process taskProcess = Runtime.getRuntime().exec("ps -e | wc -l");
                taskProcess.waitFor();
                BufferedReader taskReader = new BufferedReader(new InputStreamReader(taskProcess.getInputStream()));
                String taskLine;
                if ((taskLine = taskReader.readLine()) != null) {
                    availableProcessors = Integer.parseInt(taskLine.trim());
                }

                Process sleepingProcess = Runtime.getRuntime().exec("ps -e | grep -c 'S<'");
                sleepingProcess.waitFor();
                BufferedReader sleepingReader = new BufferedReader(new InputStreamReader(sleepingProcess.getInputStream()));
                String sleepingLine;
                if ((sleepingLine = sleepingReader.readLine()) != null) {
                    availableProcessors += Integer.parseInt(sleepingLine.trim());
                }

                Process zombieProcess = Runtime.getRuntime().exec("ps -e | grep -c 'Z<'");
                zombieProcess.waitFor();
                BufferedReader zombieReader = new BufferedReader(new InputStreamReader(zombieProcess.getInputStream()));
                String zombieLine;
                if ((zombieLine = zombieReader.readLine()) != null) {
                    availableProcessors += Integer.parseInt(zombieLine.trim());
                }

                // Pobierz temperaturę CPU (to tylko przykład, może nie działać na wszystkich systemach)
                Process temperatureProcess = Runtime.getRuntime().exec("sensors | grep 'Package id 0'");
                temperatureProcess.waitFor();
                BufferedReader temperatureReader = new BufferedReader(new InputStreamReader(temperatureProcess.getInputStream()));
                String temperatureLine;
                if ((temperatureLine = temperatureReader.readLine()) != null) {
                    // Przykładowe parsowanie temperatury
                    // String temperature = temperatureLine.split(":")[1].trim();
                }

                // Pobierz taktowanie procesora (to tylko przykład, może nie działać na wszystkich systemach)
                Process cpuSpeedProcess = Runtime.getRuntime().exec("cat /proc/cpuinfo | grep 'cpu MHz'");
                cpuSpeedProcess.waitFor();
                BufferedReader cpuSpeedReader = new BufferedReader(new InputStreamReader(cpuSpeedProcess.getInputStream()));
                String cpuSpeedLine;
                if ((cpuSpeedLine = cpuSpeedReader.readLine()) != null) {
                    // Przykładowe parsowanie taktowania procesora
                    // String cpuSpeed = cpuSpeedLine.split(":")[1].trim();
                }

                // Pobierz zużycie pamięci (to tylko przykład, może nie działać na wszystkich systemach)
                Process memoryUsageProcess = Runtime.getRuntime().exec("free | grep Mem");
                memoryUsageProcess.waitFor();
                BufferedReader memoryUsageReader = new BufferedReader(new InputStreamReader(memoryUsageProcess.getInputStream()));
                String memoryUsageLine;
                if ((memoryUsageLine = memoryUsageReader.readLine()) != null) {
                    // Przykładowe parsowanie zużycia pamięci
                    // String memoryUsage = memoryUsageLine.split("\\s+")[2].trim();
                }

            } else {
                availableProcessors = Runtime.getRuntime().availableProcessors();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableProcessors;
    }
}
