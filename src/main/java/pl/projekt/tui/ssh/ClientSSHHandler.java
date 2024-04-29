package pl.projekt.tui.ssh;

import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.SignalListener;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.component.*;
import pl.projekt.tui.logic.ProcessListUpdater;
import pl.projekt.tui.model.color.ANSIColors;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientSSHHandler implements Command {

    private final Logger logger = LoggerFactory.getLogger(ClientSSHHandler.class);
    private InputStream in;
    private OutputStream out, errout;
    private ChannelSession session;
    private Environment environment;
    private TUIScreen tuiScreen;
    private TUIManager TUIManager;
    private int ScreenWidth = 1200;
    private int ScreenHeight = 800;
    private ExitCallback exitCallback;
    private final BlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();
    private Thread receiverThread, senderThread;

    public ClientSSHHandler() {
        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);
    }

    public void init() {
        try {
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.addLayer(3);
            tuiScreen.setBgColor(ANSIColors.BG_BRIGHT_BLACK.getCode(), 0);
            TUITab op1 = new TUITab("F1 Process manager", 0, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUITab op2 = new TUITab("F2 Information", 20, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUITab op3 = new TUITab("F3 Search", 30, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUITab op4 = new TUITab("F4 Filter", 40, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUITab op5 = new TUITab("F5 Sort By", 50, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUITab op6 = new TUITab("F6 Quick", 60, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            TUIManager.addTab(op1);
            TUIManager.addTab(op2);
            TUIManager.addTab(op3);
            TUIManager.addTab(op4);
            TUIManager.addTab(op5);
            TUIManager.addTab(op6);
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    List<List<String>> processData = ProcessListUpdater.getProcessData();
                    List<String> newLabels = processData.get(0);
                    processData.remove(0);
                    List<String> rowData = new ArrayList<>();
                    for (List<String> row : processData) {
                        rowData.addAll(row);
                    }
                    TUITabela newTable = new TUITabela(10, 7, 100, 10, 0, TUIManager, newLabels, rowData);
                    newTable.setVisibleRows(Math.min(10, processData.size()));
                    newTable.drawAllHeaders(op1);
                    newTable.drawAllRows(op1);
                    if (processData.size() > 10) {
                        TUIScrollBar scrollBar = new TUIScrollBar(newTable);
                        op1.addComponent(scrollBar);
                    }
                    op1.removeComponent(newTable);
                    op1.addComponent(newTable);
                    TUIManager.addTab(op1);
                    TUIManager.initialize();
                    TUIManager.refresh();
                } catch (Exception e) {
                    logger.error("Błąd aktualizacji tabeli: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 0, 10, TimeUnit.SECONDS);
            int availableProcessors = ProcessListUpdater.getAvailableProcessors();
            TUILabel labelCores = new TUILabel("Available Processors: " + availableProcessors, 0, 3, 0, ANSIColors.BG_BRIGHT_BLACK.getCode(), TUIManager);
            op2.addComponent(labelCores);
            String cpuTemperature = "CPU Temperature: ";
            TUILabel labelCPUTemperature = new TUILabel(cpuTemperature, 0, 4, 0, ANSIColors.BG_BRIGHT_BLACK.getCode(), TUIManager);
            op2.addComponent(labelCPUTemperature);
            String cpuSpeed = "CPU Speed: ";
            TUILabel labelCPUSpeed = new TUILabel(cpuSpeed, 0, 5, 0, ANSIColors.BG_BRIGHT_BLACK.getCode(), TUIManager);
            op2.addComponent(labelCPUSpeed);
            String memoryUsage = "Memory Usage: ";
            TUILabel labelMemoryUsage = new TUILabel(memoryUsage, 0, 6, 0, ANSIColors.BG_BRIGHT_BLACK.getCode(), TUIManager);
            op2.addComponent(labelMemoryUsage);
            TUIManager.addTab(op2);
            TUIManager.addTab(op3);
            TUIManager.addTab(op4);
            TUIManager.addTab(op5);
            TUIManager.addTab(op6);
            TUIManager.initialize();
        } catch (Exception e) {
            logger.error("Błąd inicjalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWindowSize(byte[] buffer, int i) {
        int width = ((buffer[i + 3] & 0xff) << 8) | (buffer[i + 4] & 0xff);
        int height = ((buffer[i + 5] & 0xff) << 8) | (buffer[i + 6] & 0xff);
        ScreenWidth = width;
        ScreenHeight = height;
        logger.info("Received window size: " + width + "x" + height);
    }

    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.errout = outputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
        TUIManager = new TUIManager(tuiScreen, out);
    }

    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        this.session = channelSession;
        this.environment = environment;
        Map<String, String> env = environment.getEnv();
        this.environment.addSignalListener(new SignalListener() {
            @Override
            public void signal(Channel channel, Signal signal) {
                try {
                    messages.put(new byte[]{(byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 255});
                } catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        }, Signal.WINCH);

        try {
            this.ScreenHeight = Integer.parseInt(env.get("LINES"));
            this.ScreenWidth = Integer.parseInt(env.get("COLUMNS"));
            TUIManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
        } catch (NumberFormatException e){
            logger.info(e.getLocalizedMessage());
        }

        init();
        receiverThread = new Thread(this::receiver);
        senderThread = new Thread(this::interpreter);

        receiverThread.start();
        senderThread.start();
    }

    @Override
    public void destroy(ChannelSession channelSession) {
        try {
            if (receiverThread != null && receiverThread.isAlive())
                receiverThread.interrupt();
            if (senderThread != null && senderThread.isAlive())
                senderThread.interrupt();
        } catch (Exception e){
            logger.error("An exception occured while destroying session " + e.getMessage());
        }
        finally {
            if (exitCallback != null)
                exitCallback.onExit(0);
            logger.info("Client disconnected due to CTRL + C");
        }

    }

    private void receiver(){
        try{
            byte[] buf = new byte[1024];
            int bytesread;
            while((bytesread = in.read(buf)) != -1){
                byte[] tmp = new byte[bytesread];
                System.arraycopy(buf, 0, tmp, 0, bytesread);
                messages.put(tmp);
            }
        } catch (InterruptedException ignored){
            logger.info("Receiver interrupted");
        }
        catch (Exception e){
            logger.error(e.getMessage() + e.getLocalizedMessage());
        }
        finally {
            logger.info("Receiver thread finished!");
        }
    }

    private void interpreter(){
        try {
            while(receiverThread.isAlive() || !messages.isEmpty()) {
                byte[] data = messages.take();
                int[] intData = new int[data.length];
                for(int i=0;i < data.length; ++i)
                    intData[i] = data[i] & 0xFF;

                logger.info("Odebrano sekwencję " + Arrays.toString(intData));
            }
        } catch (InterruptedException e){
            logger.info("Sender thread finished!");
        } catch (Exception e) {
            e.printStackTrace();
            destroy(session);
        }
    }
}
