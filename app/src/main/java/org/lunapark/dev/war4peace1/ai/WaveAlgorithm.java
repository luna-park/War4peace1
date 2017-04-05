package org.lunapark.dev.war4peace1.ai;

import static org.lunapark.dev.war4peace1.utils.Consts.BOT_RANGE;
import static org.lunapark.dev.war4peace1.utils.Consts.GRID_MULTIPLIER;

/**
 * Алгоритм волновой трассировки (волновой алгоритм, алгоритм Ли)
 * @link https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D0%9B%D0%B8
 * Created by znak on 05.04.2017.
 */

public class WaveAlgorithm {
    private int size = BOT_RANGE * GRID_MULTIPLIER; // размер сетки

    private WaveCell[][] grid;
    private WaveCell startCell, finishCell;

    public WaveAlgorithm() {
        grid = new WaveCell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WaveCell waveCell = new WaveCell();
                waveCell.x = i;
                waveCell.y = j;
                waveCell.value = -2;
                grid[i][j] = waveCell;
            }
        }
    }

    // Fill grid. Walls, start, finish must be set.
    // TODO
    public void fillGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WaveCell waveCell = grid[i][j];
                int value = waveCell.value;
                if (value == -2) {
                    int newValue = value + 1;
                    if (i > 1) {
                        grid[i - 1][j].value = newValue;
                    }
                }
            }
        }
    }

    private void clear() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WaveCell waveCell = grid[i][j];
                waveCell.value = -2;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public WaveCell[][] getGrid() {
        return grid;
    }

    public WaveCell getStartCell() {
        return startCell;
    }

    public void setStartCell(WaveCell startCell) {
        this.startCell = startCell;
    }

    public WaveCell getFinishCell() {
        return finishCell;
    }

    public void setFinishCell(WaveCell finishCell) {
        this.finishCell = finishCell;
    }
}