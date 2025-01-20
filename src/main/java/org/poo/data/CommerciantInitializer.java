package org.poo.data;

import org.poo.commerciants.Commerciant;
import org.poo.fileio.CommerciantInput;

import java.util.ArrayList;

/**
 * Utility class for initializing and loading commerciants
 */
public final class CommerciantInitializer {

    /**
     * Loads a list of commerciants from the provided input data and registers them in the
     * DataContainer
     *
     * @param commerciantInputs An array of CommerciantInput objects containing
     *                          data for commerciant initialization
     * @param dataContainer     The DataContainer where commerciants will be registered
     * @return A list of Commerciant objects created based on the input data.
     */
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
