/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.Exception;

/**
 *
 * @author nuno.sousa
 */
public class NotExpectedException extends RuntimeException {
    public NotExpectedException(){
        super();
    }
    public NotExpectedException(String message){
        super(message);
    }
}
