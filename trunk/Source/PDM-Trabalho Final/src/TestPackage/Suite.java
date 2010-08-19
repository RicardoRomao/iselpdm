/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TestPackage;

import jmunit.framework.cldc10.TestSuite;
import storage.RecStoreRepository;

/**
 *
 * @author nuno.sousa
 */
public class Suite extends TestSuite {

    public Suite(){
        super("PDM Tests");
        RecStoreRepository.clearRecStores();
        add(new ItemRecordStoreTest());
        add(new OwnItemRecordStoreTest());
    }
}
