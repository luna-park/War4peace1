package org.lunapark.dev.war4peace1.ai;

/**
 * Ячейка для волнового алгоритма
 * Created by znak on 05.04.2017.
 */

public class WaveCell {
    public int x, y; // координаты ячейки
    public int value; // номер шага или 0 - старт, -1 - стена, -2 - пусто, -3 - финиш
}
