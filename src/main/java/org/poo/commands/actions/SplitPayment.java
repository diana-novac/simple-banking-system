package org.poo.commands.actions;

import org.poo.fileio.CommandInput;
import org.poo.main.App;
import org.poo.split.SplitPaymentRequest;

public final class SplitPayment implements ActionCommand {
    @Override
    public void execute(final App app, final CommandInput command) {
        SplitPaymentRequest req = new SplitPaymentRequest(app, command);
        app.addSplitPayment(req);
    }
}
