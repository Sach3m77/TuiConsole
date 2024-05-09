package pl.projekt.tui.ssh;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import pl.projekt.tui.component.*;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;
import pl.projekt.tui.model.keys.KeyboardHandler;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class ClientSSHHandler implements Command {
    private final List<TUITab> tabs = new ArrayList<>();
    private InputStream in;
    private OutputStream out, errout;
    private ChannelSession session;
    private Environment environment;
    private final TUIScreen tuiScreen;
    private TUIManager TUIManager;
    private int ScreenWidth = 1200;
    private int ScreenHeight = 800;
    private ExitCallback exitCallback;
    private final BlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();
    private final KeyboardHandler keyboardHandler = new KeyboardHandler();
    private Thread receiverThread, senderThread;
    private TUITab currentTab;

    public ClientSSHHandler() {
        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);
    }


    /*
    |--------------------------------------------------------|
    |PRZYCISK MOZNA DODAC DO GENEROWANIA RAPORTÓW FINANSOWYCH|
    |--------------------------------------------------------|
     */

    public void init() {
        try {
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.addLayer(3);

            tuiScreen.setBgColor(Colors.BG_BRIGHT_WHITE.getCode(), 0);

            TUITab op1 = new TUITab("F1 Main Page", 0, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op1);
            currentTab = tabs.get(0);
            TUITab op2 = new TUITab("F2 Credit calculator", 20, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op2);
            TUITab op3 = new TUITab("F3 Savings calculator", 40, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op3);
            TUITab op4 = new TUITab("F4 Investment calculator", 60, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op4);
            TUITab op5 = new TUITab("F5 Currency converter", 80, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op5);
            TUITab op6 = new TUITab("F6 Tax calculator", 100, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op6);
            TUITab op7 = new TUITab("F7 Pension calculator", 120, 0, ScreenWidth, ScreenHeight, 0, TUIManager);
            tabs.add(op7);

            TUIManager.addTab(op1);
            TUIManager.addTab(op2);
            TUIManager.addTab(op3);
            TUIManager.addTab(op4);
            TUIManager.addTab(op5);
            TUIManager.addTab(op6);
            TUIManager.addTab(op7);

            TUILabel labelProjectTitle = new TUILabel("TUI Financial Calculator: ", 50, 2, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelProjectTitle);

            TUILabel labelSubjectTitle = new TUILabel("Programing Defence ", 50, 3, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelSubjectTitle);

            TUILabel labelOptions = new TUILabel("Options: ", 0, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOptions);

            TUILabel labelOption1 = new TUILabel("-Credit calculator: Calculate the amount of the monthly loan installment based on the loan amount, interest rate and repayment period.", 5, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption1);
            TUILabel labelOption2 = new TUILabel("-Savings calculator: Calculate the future value of your savings based on regular payments, interest rate and savings period.", 5, 9, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption2);
            TUILabel labelOption3 = new TUILabel("-Investment calculator: Calculate potential ROI based on initial capital, investment period and expected ROI.", 5, 11, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption3);
            TUILabel labelOption4 = new TUILabel("-Currency converter: Convert amounts between different currencies based on current exchange rates.", 5, 13, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption4);
            TUILabel labelOption5 = new TUILabel("-Tax calculator: Calculate your income tax amount based on your income and applicable tax rates.", 5, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption5);
            TUILabel labelOption6 = new TUILabel("-Pension calculator: Calculate the future value of your pension based on your current age, planned savings and expected retirement age.", 5, 17, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op1.addComponent(labelOption6);

            TUILabel labelAmountOfCredit = new TUILabel("Amount of credit:", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op2.addComponent(labelAmountOfCredit);
            TUILabel labelLoanInterestRate = new TUILabel("Loan interest rate (annual):", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op2.addComponent(labelLoanInterestRate);
            TUILabel labelLoanRepaymentPeriod = new TUILabel("Loan repayment period (in years):", 50, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op2.addComponent(labelLoanRepaymentPeriod);

            TUITextField amountOfCredit = new TUITextField(110, 5, 10, 1, 0, TUIManager);
            TUITextField loanInterestRate = new TUITextField(110, 6, 10, 1, 0, TUIManager);
            TUITextField loanRepaymentPeriod = new TUITextField(110, 7, 10, 1, 0, TUIManager);

            amountOfCredit.setNumeric(true);
            loanInterestRate.setNumeric(true);
            loanRepaymentPeriod.setNumeric(true);

            TUIButton calculateButton = new TUIButton(
                    100, 10, 15, 5, 0, "Calculate",
                    () -> {
                        try {

                            double amountOfCreditInput = amountOfCredit.getParsedNumber();
                            double loanInterestRateInput = loanInterestRate.getParsedNumber();
                            double loanRepaymentPeriodInput = loanRepaymentPeriod.getParsedNumber();

                            double monthlyPayment = calculateMonthlyPayment(amountOfCreditInput, loanInterestRateInput, loanRepaymentPeriodInput);

                            System.out.println("Monthly Payment: " + monthlyPayment);

                            TUILabel resultLabel = new TUILabel("Monthly Payment: " + monthlyPayment, 50, 10, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
                            op2.addComponent(resultLabel);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    TUIManager
            );


            op2.addComponent(amountOfCredit);
            op2.addComponent(loanInterestRate);
            op2.addComponent(loanRepaymentPeriod);
            op2.addComponent(calculateButton);


            TUILabel labelRegularPayments = new TUILabel("Regular payments (monthly):", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op3.addComponent(labelRegularPayments);

            TUILabel labelSavingsInterestRate = new TUILabel("Savings interest rate (annual):", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op3.addComponent(labelSavingsInterestRate);

            TUILabel labelSavingPeriod = new TUILabel("Saving period (in years):", 50, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op3.addComponent(labelSavingPeriod);


            TUITextField regularPaymentsField = new TUITextField(110, 5, 10, 1, 0, TUIManager);
            regularPaymentsField.setNumeric(true);
            op3.addComponent(regularPaymentsField);

            TUITextField savingsInterestRateField = new TUITextField(110, 6, 10, 1, 0, TUIManager);
            savingsInterestRateField.setNumeric(true);
            op3.addComponent(savingsInterestRateField);

            TUITextField savingPeriodField = new TUITextField(110, 7, 10, 1, 0, TUIManager);
            savingPeriodField.setNumeric(true);
            op3.addComponent(savingPeriodField);


            TUIButton calculateSavingsButton = new TUIButton(
                    100, 10, 20, 3, 0, "Calculate Savings",
                    () -> {
                        try {
                            double regularPayments = regularPaymentsField.getParsedNumber();
                            double annualInterestRate = savingsInterestRateField.getParsedNumber();
                            double savingPeriodInYears = savingPeriodField.getParsedNumber();

                            double futureValue = calculateFutureValueOfSavings(regularPayments, annualInterestRate, savingPeriodInYears);

                            TUILabel futureValueLabel = new TUILabel("Future Value of Savings: " + futureValue, 50, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
                            op3.addComponent(futureValueLabel);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    TUIManager
            );
            op3.addComponent(calculateSavingsButton);



            TUILabel labelInitialInvestmentCapital = new TUILabel("Initial investment capital:", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op4.addComponent(labelInitialInvestmentCapital);

            TUILabel labelInvestmentPeriod = new TUILabel("Investment period (in years):", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op4.addComponent(labelInvestmentPeriod);

            TUILabel labelExpectedRateOfReturnOnInvestment = new TUILabel("Expected rate of return on investment (annual):", 50, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op4.addComponent(labelExpectedRateOfReturnOnInvestment);


            TUITextField initialInvestmentCapitalField = new TUITextField(110, 5, 10, 1, 0, TUIManager);
            initialInvestmentCapitalField.setNumeric(true);
            op4.addComponent(initialInvestmentCapitalField);

            TUITextField investmentPeriodField = new TUITextField(110, 6, 10, 1, 0, TUIManager);
            investmentPeriodField.setNumeric(true);
            op4.addComponent(investmentPeriodField);

            TUITextField expectedRateOfReturnField = new TUITextField(110, 7, 10, 1, 0, TUIManager);
            expectedRateOfReturnField.setNumeric(true);
            op4.addComponent(expectedRateOfReturnField);

            TUIButton calculateInvestmentReturnButton = new TUIButton(
                    100, 10, 20, 3, 0, "Calculate Investment Return",
                    () -> {
                        try {
                            double initialInvestmentCapital = initialInvestmentCapitalField.getParsedNumber();
                            double investmentPeriod = investmentPeriodField.getParsedNumber();
                            double expectedRateOfReturn = expectedRateOfReturnField.getParsedNumber();

                            double investmentReturn = calculateInvestmentReturn(initialInvestmentCapital, investmentPeriod, expectedRateOfReturn);

                            TUILabel investmentReturnLabel = new TUILabel("Investment Return: " + investmentReturn, 50, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
                            op4.addComponent(investmentReturnLabel);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    TUIManager
            );
            op4.addComponent(calculateInvestmentReturnButton);

            TUILabel labelAmountToBeConvertedl = new TUILabel("Amount to be converted:", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op5.addComponent(labelAmountToBeConvertedl);
            TUILabel labelSelectingTheSourceCurrency = new TUILabel("Selecting the source currency:", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op5.addComponent(labelSelectingTheSourceCurrency);
            TUILabel labelSelectingTheTargetCurrency = new TUILabel("Selecting the target currency:", 50, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op5.addComponent(labelSelectingTheTargetCurrency);

            TUILabel labelAnnualIncome = new TUILabel("Annual income:", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op6.addComponent(labelAnnualIncome);

            TUILabel labelTaxRatesApplied = new TUILabel("Tax rates applied:", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op6.addComponent(labelTaxRatesApplied);

            TUITextField annualIncomeField = new TUITextField(110, 5, 10, 1, 0, TUIManager);
            annualIncomeField.setNumeric(true);
            op6.addComponent(annualIncomeField);

            TUITextField taxRatesAppliedField = new TUITextField(110, 6, 10, 1, 0, TUIManager);
            taxRatesAppliedField.setNumeric(true);
            op6.addComponent(taxRatesAppliedField);

            TUIButton calculateTaxButton = new TUIButton(
                    100, 10, 20, 3, 0, "Calculate Tax",
                    () -> {
                        try {
                            double annualIncome = annualIncomeField.getParsedNumber();
                            double taxRatesApplied = taxRatesAppliedField.getParsedNumber();

                            double taxAmount = calculateTax(annualIncome, taxRatesApplied);

                            TUILabel taxAmountLabel = new TUILabel("Tax Amount: " + taxAmount, 50, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
                            op6.addComponent(taxAmountLabel);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    TUIManager
            );
            op6.addComponent(calculateTaxButton);


            TUILabel labelCurrentAge = new TUILabel("Current age:", 50, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op7.addComponent(labelCurrentAge);

            TUILabel labelPlannedSavingsForRetirement = new TUILabel("Planned savings for retirement (annual):", 50, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op7.addComponent(labelPlannedSavingsForRetirement);

            TUILabel labelExpectedRetirementAge = new TUILabel("Expected retirement age:", 50, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
            op7.addComponent(labelExpectedRetirementAge);

            TUITextField currentAgeField = new TUITextField(110, 5, 10, 1, 0, TUIManager);
            currentAgeField.setNumeric(true);
            op7.addComponent(currentAgeField);

            TUITextField plannedSavingsForRetirementField = new TUITextField(110, 6, 10, 1, 0, TUIManager);
            plannedSavingsForRetirementField.setNumeric(true);
            op7.addComponent(plannedSavingsForRetirementField);

            TUITextField expectedRetirementAgeField = new TUITextField(110, 7, 10, 1, 0, TUIManager);
            expectedRetirementAgeField.setNumeric(true);
            op7.addComponent(expectedRetirementAgeField);

            TUIButton calculatePensionButton = new TUIButton(
                    100, 10, 20, 3, 0, "Calculate Pension",
                    () -> {
                        try {
                            int currentAge = (int) currentAgeField.getParsedNumber();
                            double plannedSavingsForRetirement = plannedSavingsForRetirementField.getParsedNumber();
                            int expectedRetirementAge = (int) expectedRetirementAgeField.getParsedNumber();

                            double futurePensionValue = calculatePension(currentAge, plannedSavingsForRetirement, expectedRetirementAge);

                            TUILabel futurePensionLabel = new TUILabel("Future Pension Value: " + futurePensionValue, 50, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), TUIManager);
                            op7.addComponent(futurePensionLabel);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    TUIManager
            );
            op7.addComponent(calculatePensionButton);



            TUIManager.addTab(op1);
            TUIManager.addTab(op2);
            TUIManager.addTab(op3);
            TUIManager.addTab(op4);
            TUIManager.addTab(op5);
            TUIManager.addTab(op6);
            TUIManager.addTab(op7);

            TUIManager.initialize();
        } catch (Exception e) {
            log.error("Błąd inicjalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double calculateMonthlyPayment(double loanAmount, double annualInterestRate, double loanPeriodInYears) {
        double monthlyInterestRate = annualInterestRate / 12 / 100;

        double loanPeriodInMonths = loanPeriodInYears * 12;

        return  (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -loanPeriodInMonths));
    }

    private double calculateFutureValueOfSavings(double regularPayments, double annualInterestRate, double savingPeriodInYears) {
        // Przyjmujemy, że oprocentowanie jest podawane w skali procentowej, np. 5% = 5
        double monthlyInterestRate = annualInterestRate / 100 / 12;
        double totalNumberOfPayments = savingPeriodInYears * 12;

        // FV = P * ((1 + r)^n - 1) / r
        return regularPayments * ((Math.pow(1 + monthlyInterestRate, totalNumberOfPayments) - 1) / monthlyInterestRate);
    }

    private double calculateInvestmentReturn(double initialCapital, double period, double rateOfReturn) {
        double finalCapital = initialCapital * Math.pow(1 + rateOfReturn, period);
        return finalCapital - initialCapital;
    }

    private double calculateTax(double annualIncome, double taxRatesApplied) {
        // Zakładając, że taxRatesApplied jest podawane jako liczba z zakresu 0-100 (np. 20.0 dla 20%)
        double taxRate = taxRatesApplied / 100;
        return annualIncome * taxRate;
    }

    private double calculatePension(int currentAge, double plannedSavingsForRetirement, int expectedRetirementAge) {
        int yearsInRetirement = 20;

        double annualPension = plannedSavingsForRetirement / yearsInRetirement;

        return annualPension * (expectedRetirementAge - currentAge);
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
        this.environment.addSignalListener((channel, signal) -> {
            try {
                messages.put(new byte[]{(byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 255});
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }, Signal.WINCH);

        try {
            this.ScreenHeight = Integer.parseInt(env.get("LINES"));
            this.ScreenWidth = Integer.parseInt(env.get("COLUMNS"));
            TUIManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
        } catch (NumberFormatException e){
            log.info(e.getLocalizedMessage());
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
            log.error("An exception occured while destroying session " + e.getMessage());
        }
        finally {
            if (exitCallback != null)
                exitCallback.onExit(0);
            log.info("Client disconnected due to CTRL + C");
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
            log.info("Receiver interrupted");
        }
        catch (Exception e){
            log.error(e.getMessage() + e.getLocalizedMessage());
        }
        finally {
            log.info("Receiver thread finished!");
        }
    }

    private void interpreter(){
        try {
            while(receiverThread.isAlive() || !messages.isEmpty()) {
                byte[] data = messages.take();
                int[] intData = new int[data.length];
                for(int i=0;i < data.length; ++i)
                    intData[i] = data[i] & 0xFF;

                KeyInfo keyInfo = keyboardHandler.getKeyInfo(intData);

                log.info("Odebrano sekwencję " + Arrays.toString(intData));

                if(keyInfo != null){
                    if(keyInfo.getLabel() == KeyLabel.INTERNAL_WIN_RESIZE) {
                        try {
                            Map<String, String> env = environment.getEnv();
                            int height = Integer.parseInt(env.get("LINES"));
                            int width = Integer.parseInt(env.get("COLUMNS"));
                            if(width != ScreenWidth || height != ScreenHeight) {
                                this.ScreenHeight = height;
                                this.ScreenWidth = width;
                                TUIManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
                            }
                        } catch (NumberFormatException e){
                            log.info(e.getLocalizedMessage());
                        }
                    } else if (keyInfo.getLabel() == KeyLabel.CTRL_C) {
                        log.info("Destroying session");
                        destroy(session);
                        break;
                    } else {
                        log.info("Odebrano klawisz " + keyInfo);
                        TUIManager.handleKeyboardInput(keyInfo);
                    }
                }
                else {
                    log.warn("Nieznana sekwencja klawiszy " + Arrays.toString(intData));
                }
            }
        } catch (InterruptedException e){
            log.info("Sender thread finished!");
        } catch (Exception e) {
            e.printStackTrace();
            destroy(session);
        }
    }
}
