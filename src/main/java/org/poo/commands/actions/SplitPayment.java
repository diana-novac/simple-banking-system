package org.poo.commands.actions;

import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.split.SplitPaymentRequest;

/**
 * Command for initiating a split payment request within the application
 */
public final class SplitPayment implements ActionCommand {

    /**
     * Executes the split payment command.
     * Creates a new split payment request and adds it to the application's
     * queue of active split payments
     * @param app     The application context
     * @param command The input containing details for the split payment request
     */
    @Override
    public void execute(final App app, final CommandInput command) {
        // Create a new split payment request based on the command
        SplitPaymentRequest req = new SplitPaymentRequest(app, command);
        if (!app.getActiveSplitPayments().contains(req)) {
            app.addSplitPayment(req);
        }
    }
}
