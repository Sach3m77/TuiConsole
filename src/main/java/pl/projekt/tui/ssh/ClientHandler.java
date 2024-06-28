package pl.projekt.tui.ssh;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.component.*;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;
import pl.projekt.tui.model.keys.KeyboardHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Handles SSH commands and manages the text-based user interface (TUI) for SSH clients.
 * Implements Apache SSHD's {@link Command} interface.
 */
@Slf4j
public class ClientHandler implements Command {
    private final List<TUITab> tabs = new ArrayList<>();
    private InputStream in;
    private OutputStream out, errout;
    private ChannelSession session;
    private Environment environment;
    private final TUIScreen tuiScreen;
    @Getter
    private TUIManager tuiManager;
    @Getter
    private int ScreenWidth = 1200;
    @Getter
    private int ScreenHeight = 800;
    private ExitCallback exitCallback;
    private final BlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();
    private final KeyboardHandler keyboardHandler = new KeyboardHandler();
    private Thread receiverThread = new Thread(this::receiver);
    private Thread senderThread = new Thread(this::interpreter);
    private TUITab currentTab;
    private static TUIDialog currentDialog = null;
    private static TUIComponent tuiComponent;
    /**
     * Constructs a new instance of {@code ClientSSHHandler} with a default screen size.
     */
    public ClientHandler() {
        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);
    }

    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    /**
     * Initializes the SSH client handler.
     * This method can be overridden to set up any initial configurations or UI components.
     */
    public void init() {
        try {
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.addLayer(3);

            tuiScreen.setBgColor(Colors.BG_BRIGHT_WHITE.getCode(), 0);

            TUITab op1 = new TUITab("F1 Main", 0, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op1);
            currentTab = tabs.get(0);
            TUITab op2 = new TUITab("F2 Credit", 20, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op2);
            TUITab op3 = new TUITab("F3 Savings", 40, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op3);
            TUITab op4 = new TUITab("F4 Investment", 60, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op4);
            TUITab op5 = new TUITab("F5 Currency", 80, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op5);
            TUITab op6 = new TUITab("F6 Tax", 100, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op6);
            TUITab op7 = new TUITab("F7 Pension", 120, 0, ScreenWidth, ScreenHeight, 0, tuiManager);
            tabs.add(op7);

            tuiManager.addTab(op1);
            tuiManager.addTab(op2);
            tuiManager.addTab(op3);
            tuiManager.addTab(op4);
            tuiManager.addTab(op5);
            tuiManager.addTab(op6);
            tuiManager.addTab(op7);

            // Strona 1: op1
            TUIBorder border1 = new TUIBorder(0, 5, 142, 15, 0, tuiManager);
            border1.setBgColor(Colors.BG_RED.getCode());
            border1.setTextColor(Colors.TEXT_WHITE.getCode());

            TUILabel labelProjectTitle = new TUILabel("TUI Financial Calculator: ", 50, 2, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelProjectTitle);

            TUILabel labelSubjectTitle = new TUILabel("Programing Defence ", 50, 3, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelSubjectTitle);

            TUILabel labelOptions = new TUILabel("Options: ", 1, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOptions);

            TUILabel labelOption1 = new TUILabel("-Credit calculator: Calculate the amount of the monthly loan installment based on the loan amount, interest rate and repayment period.", 5, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption1);
            TUILabel labelOption2 = new TUILabel("-Savings calculator: Calculate the future value of your savings based on regular payments, interest rate and savings period.", 5, 9, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption2);
            TUILabel labelOption3 = new TUILabel("-Investment calculator: Calculate potential ROI based on initial capital, investment period and expected ROI.", 5, 11, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption3);
            TUILabel labelOption4 = new TUILabel("-Currency converter: Convert amounts between different currencies based on current exchange rates.", 5, 13, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption4);
            TUILabel labelOption5 = new TUILabel("-Tax calculator: Calculate your income tax amount based on your income and applicable tax rates.", 5, 15, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption5);
            TUILabel labelOption6 = new TUILabel("-Pension calculator: Calculate the future value of your pension based on your current age, planned savings and expected retirement age.", 5, 17, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op1.addComponent(labelOption6);




            TUIBorder border2 = new TUIBorder(27, 3, 80, 10, 0, tuiManager);
            border2.setBgColor(Colors.BG_RED.getCode());
            border2.setTextColor(Colors.TEXT_WHITE.getCode());
            op2.addComponent(border2);
            op3.addComponent(border2);
            op6.addComponent(border2);
            op7.addComponent(border2);

            TUIBorder border3 = new TUIBorder(27, 3, 82, 10, 0, tuiManager);
            border3.setBgColor(Colors.BG_RED.getCode());
            border3.setTextColor(Colors.TEXT_WHITE.getCode());

            TUIBorder border4 = new TUIBorder(27, 3, 82, 11, 0, tuiManager);
            border4.setBgColor(Colors.BG_RED.getCode());
            border4.setTextColor(Colors.TEXT_WHITE.getCode());
            op4.addComponent(border4);


            // Strona 2: op2
            TUILabel labelAmountOfCredit = new TUILabel("Amount of credit:", 30, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op2.addComponent(labelAmountOfCredit);
            TUILabel labelLoanInterestRate = new TUILabel("Loan interest rate (annual):", 30, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op2.addComponent(labelLoanInterestRate);
            TUILabel labelLoanRepaymentPeriod = new TUILabel("Loan repayment period (in years):", 30, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op2.addComponent(labelLoanRepaymentPeriod);

            TUITextField amountOfCredit = new TUITextField(90, 5, 10, 1, 0, tuiManager);
            TUITextField loanInterestRate = new TUITextField(90, 6, 10, 1, 0, tuiManager);
            TUITextField loanRepaymentPeriod = new TUITextField(90, 7, 10, 1, 0, tuiManager);

            amountOfCredit.setNumeric(true);
            loanInterestRate.setNumeric(true);
            loanRepaymentPeriod.setNumeric(true);

            op2.addComponent(amountOfCredit);
            op2.addComponent(loanInterestRate);
            op2.addComponent(loanRepaymentPeriod);

            TUIButton calculateButton = new TUIButton(
                    80, 10, 15, 5, 0, "Calculate",
                    () -> {
                        try {

                            double amountOfCreditInput = amountOfCredit.getParsedNumber();
                            double loanInterestRateInput = loanInterestRate.getParsedNumber();
                            double loanRepaymentPeriodInput = loanRepaymentPeriod.getParsedNumber();

                            double monthlyPayment = calculateMonthlyPayment(amountOfCreditInput, loanInterestRateInput, loanRepaymentPeriodInput);

                            if (currentDialog != null) {
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            currentDialog = new TUIDialog(30, 15, 40, 10, 0, "Monthly Payment", monthlyPayment, tuiManager, tabs.get(tuiManager.getCurrentTab()));
                            currentDialog.setMessage("Are you accepting data?");
                            op2.addComponent(currentDialog);
                            currentDialog.show();

                            if (currentDialog.isCancelled()) {
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            tuiManager.refresh();
                            tuiManager.render();


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );


            op2.addComponent(amountOfCredit);
            op2.addComponent(loanInterestRate);
            op2.addComponent(loanRepaymentPeriod);
            calculateButton.setTextColor(Colors.TEXT_WHITE.getCode());
            op2.addComponent(calculateButton);



            // Strona 3: op3
            TUILabel labelRegularPayments = new TUILabel("Regular payments (monthly):", 30, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op3.addComponent(labelRegularPayments);

            TUILabel labelSavingsInterestRate = new TUILabel("Savings interest rate (annual):", 30, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op3.addComponent(labelSavingsInterestRate);

            TUILabel labelSavingPeriod = new TUILabel("Saving period (in years):", 30, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op3.addComponent(labelSavingPeriod);

            TUITextField regularPaymentsField = new TUITextField(90, 5, 10, 1, 0, tuiManager);
            regularPaymentsField.setNumeric(true);
            op3.addComponent(regularPaymentsField);

            TUITextField savingsInterestRateField = new TUITextField(90, 6, 10, 1, 0, tuiManager);
            savingsInterestRateField.setNumeric(true);
            op3.addComponent(savingsInterestRateField);

            TUITextField savingPeriodField = new TUITextField(90, 7, 10, 1, 0, tuiManager);
            savingPeriodField.setNumeric(true);
            op3.addComponent(savingPeriodField);

            TUIButton calculateSavingsButton = new TUIButton(
                    80, 10, 20, 3, 0, "Calculate Savings",
                    () -> {
                        try {
                            double regularPayments = regularPaymentsField.getParsedNumber();
                            double annualInterestRate = savingsInterestRateField.getParsedNumber();
                            double savingPeriodInYears = savingPeriodField.getParsedNumber();

                            double futureValue = calculateFutureValueOfSavings(regularPayments, annualInterestRate, savingPeriodInYears);

                            if (currentDialog != null) {
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            currentDialog = new TUIDialog(30, 15, 40, 10, 0, "Future Value of Savings", futureValue, tuiManager, tabs.get(tuiManager.getCurrentTab()));
                            currentDialog.setMessage("Are you accepting data?");
                            op3.addComponent(currentDialog);

                            currentDialog.show();

                            if (currentDialog.isCancelled()) {
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            tuiManager.refresh();
                            tuiManager.render();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );

            op3.addComponent(calculateSavingsButton);



            // Strona 4: op4
            TUILabel labelInitialInvestmentCapital = new TUILabel("Initial investment capital:", 30, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op4.addComponent(labelInitialInvestmentCapital);

            TUILabel labelInvestmentPeriod = new TUILabel("Investment period (in years):", 30, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op4.addComponent(labelInvestmentPeriod);

            TUILabel labelExpectedRateOfReturnOnInvestment = new TUILabel("Expected rate of return on investment (annual):", 30, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op4.addComponent(labelExpectedRateOfReturnOnInvestment);

            TUILabel showAsLabel = new TUILabel("Show as:", 30, 10, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op4.addComponent(showAsLabel);

            TUITextField initialInvestmentCapitalField = new TUITextField(90, 5, 10, 1, 0, tuiManager);
            initialInvestmentCapitalField.setNumeric(true);
            op4.addComponent(initialInvestmentCapitalField);

            TUITextField investmentPeriodField = new TUITextField(90, 6, 10, 1, 0, tuiManager);
            investmentPeriodField.setNumeric(true);
            op4.addComponent(investmentPeriodField);

            TUITextField expectedRateOfReturnField = new TUITextField(90, 7, 10, 1, 0, tuiManager);
            expectedRateOfReturnField.setNumeric(true);
            op4.addComponent(expectedRateOfReturnField);

            TUIRadioButtonGroup radioButtonGroupDisplayMethod = new TUIRadioButtonGroup(tuiManager);

            TUICheckBox tableCheckBox = new TUICheckBox(40, 9, 3, 1, "Table", "tab", tuiManager, radioButtonGroupDisplayMethod);
            op4.addComponent(tableCheckBox);
            TUICheckBox listCheckBox = new TUICheckBox(60, 9, 3, 1, "List", "list", tuiManager, radioButtonGroupDisplayMethod);
            op4.addComponent(listCheckBox);

            TUIButton calculateInvestmentReturnButton = new TUIButton(
                    80, 10, 20, 3, 0, "Calculate Investment Return",
                    () -> {
                        try {
                            double initialInvestmentCapital = initialInvestmentCapitalField.getParsedNumber();
                            double investmentPeriod = investmentPeriodField.getParsedNumber();
                            double expectedRateOfReturn = expectedRateOfReturnField.getParsedNumber();

                            String selectedDisplayMethod = radioButtonGroupDisplayMethod.getSelectedCheckBox() != null ? radioButtonGroupDisplayMethod.getSelectedCheckBox().getValue() : "tab";

                            if (currentDialog != null) {
                                //op4.setCurrentActiveComponent(0);
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            TUIComponent tuiComponent = showInvestmentReturn(initialInvestmentCapital, investmentPeriod, expectedRateOfReturn, selectedDisplayMethod);
                            Runnable task = () ->  tuiManager.addComponent(tuiComponent);

                            currentDialog = new TUIDialog(30, 15, 40, 10, 0, "Investment Return", null, tuiManager, tabs.get(tuiManager.getCurrentTab()), tuiComponent, task);
                            currentDialog.setMessage("Are you accepting data?");
                            op4.addComponent(currentDialog);
                            currentDialog.show();


                            if (currentDialog.isCancelled()) {
                                //op4.setCurrentActiveComponent(0);
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );
            op4.addComponent(calculateInvestmentReturnButton);






            // Strona 5: op5
            TUIBorder border5 = new TUIBorder(4, 2, 102, 25, 0, tuiManager);
            border5.setBgColor(Colors.BG_RED.getCode());
            border5.setTextColor(Colors.TEXT_WHITE.getCode());
            op5.addComponent(border5);

            TUIRadioButtonGroup radioButtonGroupSource = new TUIRadioButtonGroup(tuiManager);
            TUIRadioButtonGroup radioButtonGroupTarget = new TUIRadioButtonGroup(tuiManager);

            TUILabel currencyLabel1 = new TUILabel("Source currency ", 9, 3, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op5.addComponent(currencyLabel1);

            TUICheckBox checkBox1 = new TUICheckBox(10, 5, 3, 1, "Euro", "eur", tuiManager, radioButtonGroupSource);
            TUICheckBox checkBox2 = new TUICheckBox(10, 10, 3, 1, "Dolar", "usd", tuiManager, radioButtonGroupSource);
            TUICheckBox checkBox3 = new TUICheckBox(10, 15, 3, 1, "Funt", "gbp", tuiManager, radioButtonGroupSource);
            TUICheckBox checkBox4 = new TUICheckBox(10, 20, 3, 1, "Zł", "zl", tuiManager, radioButtonGroupSource);

            op5.addComponent(checkBox1);
            op5.addComponent(checkBox2);
            op5.addComponent(checkBox3);
            op5.addComponent(checkBox4);

            TUILabel currencyLabel2 = new TUILabel("Target currency ", 40, 3, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op5.addComponent(currencyLabel2);

            TUICheckBox checkBox5 = new TUICheckBox(41, 5, 3, 1, "Euro", "eur", tuiManager, radioButtonGroupTarget);
            TUICheckBox checkBox6 = new TUICheckBox(41, 10, 3, 1, "Dolar", "usd", tuiManager, radioButtonGroupTarget);
            TUICheckBox checkBox7 = new TUICheckBox(41, 15,3, 1, "Funt", "gbp", tuiManager, radioButtonGroupTarget);
            TUICheckBox checkBox8 = new TUICheckBox(41, 20, 3, 1, "Zł", "zl", tuiManager, radioButtonGroupTarget);

            op5.addComponent(checkBox5);
            op5.addComponent(checkBox6);
            op5.addComponent(checkBox7);
            op5.addComponent(checkBox8);

            TUILabel amountCurrency = new TUILabel("Amount of money:", 58, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op5.addComponent(amountCurrency);

            TUITextField amountCurrencyField = new TUITextField(75, 5, 10, 1, 0, tuiManager);
            amountCurrencyField.setNumeric(true);
            op5.addComponent(amountCurrencyField);

            TUIButton calculateSwapCurrency = new TUIButton(
                    75, 7, 10, 6, 0, "Swap  ",
                    () -> {
                        try {
                            double amountCurrencySouce = amountCurrencyField.getParsedNumber();
                            // Wybór waluty źródłowej
                            String selectedSourceCurrency = radioButtonGroupSource.getSelectedCheckBox() != null ? radioButtonGroupSource.getSelectedCheckBox().getValue() : "eur";

                            // Wybór waluty docelowej
                            String selectedTargetCurrency = radioButtonGroupTarget.getSelectedCheckBox() != null ? radioButtonGroupTarget.getSelectedCheckBox().getValue() : "eur";

                            // Sprawdzenie, czy wybrane waluty źródłowa i docelowa są niepuste
                            double amountCurrencyTarget = calculateCurrency(amountCurrencySouce, selectedSourceCurrency, selectedTargetCurrency);

                            // Pozostała część kodu dotycząca wyświetlenia dialogu i aktualizacji GUI
                            if (currentDialog != null) {
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            currentDialog = new TUIDialog(56, 14, 40, 10, 0, "Swap currency", amountCurrencyTarget, tuiManager, tabs.get(tuiManager.getCurrentTab()));
                            currentDialog.setMessage("Are you accepting data?");
                            op5.addComponent(currentDialog);

                            currentDialog.show();

                            if (currentDialog.isCancelled()) {
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );
            op5.addComponent(calculateSwapCurrency);




            // Strona 6: op6
            TUILabel labelAnnualIncome = new TUILabel("Annual income:", 30, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op6.addComponent(labelAnnualIncome);

            TUILabel labelTaxRatesApplied = new TUILabel("Tax rates applied:", 30, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op6.addComponent(labelTaxRatesApplied);

            TUITextField annualIncomeField = new TUITextField(90, 5, 10, 1, 0, tuiManager);
            annualIncomeField.setNumeric(true);
            op6.addComponent(annualIncomeField);

            TUITextField taxRatesAppliedField = new TUITextField(90, 6, 10, 1, 0, tuiManager);
            taxRatesAppliedField.setNumeric(true);
            op6.addComponent(taxRatesAppliedField);

            TUIButton calculateTaxButton = new TUIButton(
                    80, 10, 20, 3, 0, "Calculate Tax",
                    () -> {
                        try {
                            double annualIncome = annualIncomeField.getParsedNumber();
                            double taxRatesApplied = taxRatesAppliedField.getParsedNumber();

                            double taxAmount = calculateTax(annualIncome, taxRatesApplied);

                            if (currentDialog != null) {
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            currentDialog = new TUIDialog(30, 15, 40, 10, 0, "Tax Amount", taxAmount, tuiManager, tabs.get(tuiManager.getCurrentTab()));
                            currentDialog.setMessage("Are you accepting data?");
                            op6.addComponent(currentDialog);

                            currentDialog.show();

                            if (currentDialog.isCancelled()) {
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            tuiManager.refresh();
                            tuiManager.render();


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );
            op6.addComponent(calculateTaxButton);



            // Strona 7: op7
            TUILabel labelCurrentAge = new TUILabel("Current age:", 30, 5, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op7.addComponent(labelCurrentAge);

            TUILabel labelPlannedSavingsForRetirement = new TUILabel("Planned savings for retirement (annual):", 30, 6, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op7.addComponent(labelPlannedSavingsForRetirement);

            TUILabel labelExpectedRetirementAge = new TUILabel("Expected retirement age:", 30, 7, 0, Colors.BG_BRIGHT_YELLOW.getCode(), tuiManager);
            op7.addComponent(labelExpectedRetirementAge);

            TUITextField currentAgeField = new TUITextField(90, 5, 10, 1, 0, tuiManager);
            currentAgeField.setNumeric(true);
            op7.addComponent(currentAgeField);

            TUITextField plannedSavingsForRetirementField = new TUITextField(90, 6, 10, 1, 0, tuiManager);
            plannedSavingsForRetirementField.setNumeric(true);
            op7.addComponent(plannedSavingsForRetirementField);

            TUITextField expectedRetirementAgeField = new TUITextField(90, 7, 10, 1, 0, tuiManager);
            expectedRetirementAgeField.setNumeric(true);
            op7.addComponent(expectedRetirementAgeField);

            TUIButton calculatePensionButton = new TUIButton(
                    80, 10, 20, 3, 0, "Calculate Pension",
                    () -> {
                        try {
                            int currentAge = (int) currentAgeField.getParsedNumber();
                            double plannedSavingsForRetirement = plannedSavingsForRetirementField.getParsedNumber();
                            int expectedRetirementAge = (int) expectedRetirementAgeField.getParsedNumber();

                            double futurePensionValue = calculatePension(currentAge, plannedSavingsForRetirement, expectedRetirementAge);

                            if (currentDialog != null) {
                                currentDialog.hide();
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            currentDialog = new TUIDialog(30, 15, 40, 10, 0, "Future Pension Value", futurePensionValue, tuiManager, tabs.get(tuiManager.getCurrentTab()));
                            currentDialog.setMessage("Are you accepting data?");
                            op7.addComponent(currentDialog);

                            currentDialog.show();

                            if (currentDialog.isCancelled()) {
                                currentDialog.hide();
                                currentDialog = null;
                                tuiManager.refresh();
                                tuiManager.render();
                            }

                            tuiManager.refresh();
                            tuiManager.render();

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    tuiManager
            );
            op7.addComponent(calculatePensionButton);




            tuiManager.addTab(op1);
            tuiManager.addTab(op2);
            tuiManager.addTab(op3);
            tuiManager.addTab(op4);
            tuiManager.addTab(op5);
            tuiManager.addTab(op6);
            tuiManager.addTab(op7);

            tuiManager.initialize();
        } catch (Exception e) {
            log.error("Błąd inicjalizacji: {}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Calculates the monthly payment for a loan based on given parameters.
     *
     * @param loanAmount        The amount of the loan.
     * @param annualInterestRate    Annual interest rate in percentage.
     * @param loanPeriodInYears Loan period in years.
     * @return The calculated monthly payment.
     */
    private double calculateMonthlyPayment(double loanAmount, double annualInterestRate, double loanPeriodInYears) {
        double monthlyInterestRate = annualInterestRate / 12 / 100;

        double loanPeriodInMonths = loanPeriodInYears * 12;

        return  (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -loanPeriodInMonths));
    }

    /**
     * Calculates the future value of savings based on regular payments and interest rate.
     *
     * @param regularPayments     Regular payments made.
     * @param annualInterestRate  Annual interest rate in percentage.
     * @param savingPeriodInYears Savings period in years.
     * @return The future value of savings.
     */
    private double calculateFutureValueOfSavings(double regularPayments, double annualInterestRate, double savingPeriodInYears) {
        // Przyjmujemy, że oprocentowanie jest podawane w skali procentowej, np. 5% = 5
        double monthlyInterestRate = annualInterestRate / 100 / 12;
        double totalNumberOfPayments = savingPeriodInYears * 12;

        // FV = P * ((1 + r)^n - 1) / r
        return regularPayments * ((Math.pow(1 + monthlyInterestRate, totalNumberOfPayments) - 1) / monthlyInterestRate);
    }

    /**
     * Calculates the return on investment based on initial capital, period, and rate of return.
     *
     * @param initialCapital  Initial amount invested.
     * @param period          Investment period in years.
     * @param rateOfReturn    Annual rate of return.
     * @return The investment return.
     */
    private double calculateInvestmentReturn(double initialCapital, double period, double rateOfReturn) {
        double finalCapital = initialCapital * Math.pow(1 + rateOfReturn, period);
        return finalCapital - initialCapital;
    }



    /**
     * Displays a table or list showing investment returns over time.
     *
     * @param initialCapital Initial investment amount.
     * @param period         Investment period in years.
     * @param rateOfReturn   Annual rate of return.
     * @param showMethod     Display method ('tab' for table, otherwise list).
     * @return The UI component displaying investment returns.
     */
    public TUIComponent showInvestmentReturn(double initialCapital, double period, double rateOfReturn, String showMethod) {

        List<String> cells = new ArrayList<>();
        cells.add("Year");
        cells.add("Investment return");

        double capital = initialCapital;

        for (int i = 1; i <= (int)period; i++) {
            capital = capital * (1 + rateOfReturn);
            cells.add(String.valueOf(i));
            cells.add(String.valueOf(capital));
        }

        if (showMethod.equals("tab")) {

            return new TUITable(150, 3, 2, cells, 5, tuiScreen, tuiManager);

        } else {

            return new TUIList(150, 3, 1,tuiScreen, tuiManager, cells);

        }

    }


    /**
     * Retrieves the exchange rate for a given currency code using an external API.
     *
     * @param currencyCode The currency code (e.g., "USD", "EUR").
     * @return The current exchange rate.
     */
    public static double getExchangeRate(String currencyCode) {
        String apiUrl = "https://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/2024-06-27/?format=json";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.err.println("HTTP request failed with response code " + responseCode);
                return 0.0;
            }

            Scanner scanner = new Scanner(url.openStream());
            String jsonString = scanner.useDelimiter("\\A").next();
            scanner.close();

            if (jsonString == null || jsonString.isEmpty()) {
                System.err.println("Received empty or null response from the API");
                return 0.0;
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray rates = jsonObject.optJSONArray("rates");

            if (rates == null || rates.length() == 0) {
                System.err.println("Rates array is missing or empty in the JSON response");
                return 0.0;
            }

            JSONObject rateObject = rates.getJSONObject(0);
            return rateObject.getDouble("mid");

        } catch (MalformedURLException e) {
            System.err.println("Invalid URL format: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Calculates the converted amount of currency from source to target.
     *
     * @param amountCurrencySource Amount of currency to convert.
     * @param sourceCurrencyCode   Source currency code.
     * @param targetCurrencyCode   Target currency code.
     * @return Converted amount in the target currency.
     * @throws IOException If an I/O error occurs during currency conversion.
     */
    public static double calculateCurrency(double amountCurrencySource, String sourceCurrencyCode, String targetCurrencyCode) throws IOException {
        if(sourceCurrencyCode.equals(targetCurrencyCode) ){
            return  amountCurrencySource;
        }else if(sourceCurrencyCode.equals("zl")){
            double targetExchangeRate = getExchangeRate(targetCurrencyCode);
            return amountCurrencySource/targetExchangeRate;
        }else if(targetCurrencyCode.equals("zl")){
            double sourceExchangeRate = getExchangeRate(sourceCurrencyCode);
            return amountCurrencySource * sourceExchangeRate;
        } else{
            double sourceExchangeRate = getExchangeRate(sourceCurrencyCode);
            double targetExchangeRate = getExchangeRate(targetCurrencyCode);
            double amountSwap = sourceExchangeRate / targetExchangeRate;
            return amountCurrencySource * amountSwap;
        }
    }

    /**
     * Calculates the annual tax based on annual income and applied tax rate.
     *
     * @param annualIncome       Annual income amount.
     * @param taxRatesApplied    Tax rate applied (in percentage).
     * @return The calculated tax amount.
     */
    private double calculateTax(double annualIncome, double taxRatesApplied) {
        // Zakładając, że taxRatesApplied jest podawane jako liczba z zakresu 0-100 (np. 20.0 dla 20%)
        double taxRate = taxRatesApplied / 100;
        return annualIncome * taxRate;
    }


    /**
     * Calculates the annual pension based on current age, planned savings, and expected retirement age.
     *
     * @param currentAge                Current age.
     * @param plannedSavingsForRetirement Planned savings for retirement.
     * @param expectedRetirementAge     Expected retirement age.
     * @return The calculated annual pension.
     */
    private double calculatePension(int currentAge, double plannedSavingsForRetirement, int expectedRetirementAge) {
        int yearsInRetirement = 20;

        double annualPension = plannedSavingsForRetirement / yearsInRetirement;

        return annualPension * (expectedRetirementAge - currentAge);
    }

    /**
     * Sets the exit callback for handling session termination.
     *
     * @param exitCallback The callback to be set for session termination.
     */
    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    /**
     * Sets the error output stream for handling error messages.
     *
     * @param outputStream The output stream to be set for error messages.
     */
    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.errout = outputStream;
    }

    /**
     * Sets the input stream for receiving data from the client.
     *
     * @param inputStream The input stream to be set for receiving data.
     */
    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    /**
     * Sets the output stream for sending data to the client and initializes the TUI manager.
     *
     * @param outputStream The output stream to be set for sending data.
     */
    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
        tuiManager = new TUIManager(tuiScreen, out);
    }

    /**
     * Starts the SSH session handling and initializes necessary components.
     *
     * @param channelSession The SSH channel session.
     * @param environment    The environment variables of the SSH session.
     * @throws IOException If an I/O error occurs during SSH session initialization.
     */
    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        this.session = channelSession;
        this.environment = environment;
        Map<String, String> env = environment.getEnv();

        this.environment.addSignalListener((channel, signal) -> {
            try {
                messages.put(new byte[]{(byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 255});
            } catch (Exception e){
                log.error("Signal listener error: {}", e.getMessage(), e);
            }
        }, Signal.WINCH);

        try {
            this.ScreenHeight = Integer.parseInt(env.get("LINES"));
            this.ScreenWidth = Integer.parseInt(env.get("COLUMNS"));
            log.info("Screen dimensions set to width: {}, height: {}", this.ScreenWidth, this.ScreenHeight);
            tuiManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
        } catch (NumberFormatException e) {
            log.error("Error parsing screen dimensions: {}", e.getLocalizedMessage(), e);
        }

        init();  // Initialize the UI components
        startThreads();

    }

    /**
     * Cleans up resources and handles the destruction of the SSH session.
     *
     * @param channelSession The SSH channel session to destroy.
     */
    @Override
    public void destroy(ChannelSession channelSession) {
        try {
            if (receiverThread != null && receiverThread.isAlive())
                receiverThread.interrupt();
            if (senderThread != null && senderThread.isAlive())
                senderThread.interrupt();
        } catch (Exception e) {
            log.error("An exception occurred while destroying session: {}", e.getMessage(), e);
        } finally {
            if (exitCallback != null)
                exitCallback.onExit(0);
            log.info("Client disconnected due to CTRL + C");
        }
    }


    /**
     * Receives input messages from the SSH session and processes them.
     */
    private void receiver() {
        try {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                byte[] tmp = new byte[bytesRead];
                System.arraycopy(buf, 0, tmp, 0, bytesRead);
                messages.put(tmp);
            }
        } catch (InterruptedException ignored) {
            log.info("Receiver interrupted");
        } catch (Exception e) {
            log.error("Receiver error: {}", e.getMessage(), e);
        } finally {
            log.info("Receiver thread finished!");
        }
    }

    /**
     * Interprets received messages and handles keyboard input.
     */
    private void interpreter() {
        try {
            while (receiverThread.isAlive() || !messages.isEmpty()) {
                byte[] data = messages.take();
                int[] intData = new int[data.length];
                for (int i = 0; i < data.length; ++i)
                    intData[i] = data[i] & 0xFF;

                KeyInfo keyInfo = keyboardHandler.getKeyInfo(intData);

                log.info("Received sequence {}", Arrays.toString(intData));

                if (keyInfo != null) {
                    if (keyInfo.getLabel() == KeyLabel.INTERNAL_WIN_RESIZE) {
                        try {
                            Map<String, String> env = environment.getEnv();
                            int height = Integer.parseInt(env.get("LINES"));
                            int width = Integer.parseInt(env.get("COLUMNS"));
                            if (width != ScreenWidth || height != ScreenHeight) {
                                this.ScreenHeight = height;
                                this.ScreenWidth = width;
                                tuiManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
                                log.info("UI resized to width: {}, height: {}", this.ScreenWidth, this.ScreenHeight);
                            }
                        } catch (NumberFormatException e) {
                            log.error("Error parsing window resize dimensions: {}", e.getLocalizedMessage(), e);
                        }
                    } else if (keyInfo.getLabel() == KeyLabel.CTRL_C) {
                        log.info("Destroying session");
                        destroy(session);
                        break;
                    } else {
                        log.info("Received key {}", keyInfo);
                        tuiManager.handleKeyboardInput(keyInfo);
                    }
                } else {
                    log.warn("Unknown key sequence {}", Arrays.toString(intData));
                }
            }
        } catch (InterruptedException e) {
            log.info("Sender thread finished!");
        } catch (Exception e) {
            log.error("Interpreter error: {}", e.getMessage(), e);
            destroy(session);
        }
    }

    public void startThreads() {
        receiverThread.start();
        senderThread.start();
    }

}
