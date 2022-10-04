package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Smith extends Agent {
    public static String ID = "SMITH";

    public Smith() {
        id = ID;
    }
}
