package ua.edu.chdtu.deanoffice.util;

import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by user on 07.03.2018.
 */
public class PersonFullNameComparator implements Comparator<Person> {
    public int compare(Person p1, Person p2) {
        Collator ukCollator = Collator.getInstance(new Locale("uk", "UA")); //Your locale here
        ukCollator.setStrength(Collator.PRIMARY);
        return ukCollator.compare((p1.getSurname()+" "+p1.getName()+" "+p1.getPatronimic()),
                p2.getSurname()+" "+p2.getName()+" "+p2.getPatronimic()
        );
    }
}

