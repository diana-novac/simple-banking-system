package org.poo.data;

import org.poo.commerciants.Commerciant;
import org.poo.fileio.CommerciantInput;

import java.util.ArrayList;

public final class CommerciantInitializer {
    public ArrayList<Commerciant> loadCommerciants(final CommerciantInput[] commerciantInputs,
                                                   final DataContainer dataContainer) {
        ArrayList<Commerciant> commerciants = new ArrayList<>();
        for (CommerciantInput commerciantInput : commerciantInputs) {
            Commerciant commerciant = new Commerciant(commerciantInput);
            commerciants.add(commerciant);

            dataContainer.getCommerciantMap().put(commerciant.getCommerciant(), commerciant);
            dataContainer.getCommerciantAccountMap().put(commerciant.getAccount(), commerciant);
        }
        return commerciants;
    }
}
