package activejdbc;

import activejdbc.test.ActiveJDBCTest;
import activejdbc.test_models.Watermelon;
import javalite.test.jspec.ExceptionExpectation;
import org.junit.Test;

/**
 * @author Igor Polevoy
 */
public class OptimisticLockingTest extends ActiveJDBCTest {

    @Test
    public void shouldSetVersionToOneWhenCreatingNewRecord(){

        Watermelon m = new Watermelon();
        m.set("melon_type", "dark_green");
        m.saveIt();
        Watermelon m1 = (Watermelon) Watermelon.findById(1);
        a(m1.get("record_version")).shouldBeEqual(1);
    }


    @Test
    public void shouldAdvanceVersionWhenRecordIsUpdated(){

        Watermelon m = new Watermelon();
        m.set("melon_type", "dark_green");
        m.saveIt();

        m = (Watermelon)Watermelon.findById(1);
        m.set("melon_type", "red").saveIt();
        m = (Watermelon)Watermelon.findById(1);
        a(m.get("record_version")).shouldBeEqual(2);

        m = (Watermelon)Watermelon.findById(1);
        m.set("melon_type", "green").saveIt();
        m = (Watermelon)Watermelon.findById(1);
        a(m.get("record_version")).shouldBeEqual(3);

        m = (Watermelon)Watermelon.findById(1);
        m.set("melon_type", "yellow").saveIt();
        m = (Watermelon)Watermelon.findById(1);
        a(m.get("record_version")).shouldBeEqual(4);
    }

    @Test(expected = StaleModelException.class)
    public void shouldThrowExceptionWhenVersionCollisionHappens(){

        Watermelon m = new Watermelon();
        m.set("melon_type", "dark_green");
        m.saveIt();

        Watermelon m1 = (Watermelon)Watermelon.findById(1);
        final Watermelon m2 = (Watermelon)Watermelon.findById(1);

        m1.set("melon_type", "red");
        m1.saveIt();

        m2.set("melon_type", "yellow");
        m2.saveIt();  //<<<<================ this will cause the StaleModelException
    }
}