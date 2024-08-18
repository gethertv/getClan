package dev.gether.getclan.core.upgrade;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelData {

    private int level;
    private double depositAmount;


    public void deposit(double amount) {
        depositAmount += amount;
    }

    public void nextLevel() {
        level += 1;
        depositAmount = 0;
    }

    public void reset() {
        level = 0;
        depositAmount = 0;
    }
}
