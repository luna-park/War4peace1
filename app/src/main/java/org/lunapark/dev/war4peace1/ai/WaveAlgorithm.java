package org.lunapark.dev.war4peace1.ai;

/**
 * Алгоритм волновой трассировки (волновой алгоритм, алгоритм Ли)
 * https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D0%9B%D0%B8
 * Created by znak on 05.04.2017.
 */

public class WaveAlgorithm {
    private int width = 20, height = 20; // размер сетки

    private WaveCell[][] grid;
    private WaveCell startCell, finishCell;

    public WaveAlgorithm() {
        grid = new WaveCell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                WaveCell waveCell = new WaveCell();
                waveCell.x = i;
                waveCell.y = j;
                grid[i][j] = waveCell;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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