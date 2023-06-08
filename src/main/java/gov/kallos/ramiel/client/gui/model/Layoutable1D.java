package gov.kallos.ramiel.client.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Layoutable1D {
    int minSize;
    int maxSize;
    int weight;
    int size = 0;
    int pos = 0;

    Layoutable1D(int minSize, int maxSize, int weight) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.weight = weight;
    }

    void update(int minSizeNew, int maxSizeNew, int weightNew) {
        this.minSize = Math.max((int)this.minSize, (int)minSizeNew);
        this.maxSize = Math.max((int)this.maxSize, (int)maxSizeNew);
        this.weight = Math.max((int)this.weight, (int)weightNew);
    }

    int getWeight() {
        return this.weight;
    }

    public static int computeLayout(int available, Layoutable1D[] cells) {
        ArrayList flex = new ArrayList();
        int distributable = available;
        int totalWeights = 0;
        for (Layoutable1D cell : cells) {
            cell.size = cell.minSize;
            distributable -= cell.minSize;
            int weight = cell.weight;
            if (weight <= 0 || cell.size >= cell.maxSize) continue;
            totalWeights += weight;
            flex.add((Object)cell);
        }
        while (distributable > 0) {
            int prevFlexSize = flex.size();
            ArrayList currentFlex = new ArrayList((Collection)flex);
            flex.clear();
            int currentWeights = totalWeights;
            totalWeights = 0;
            int currentDistributable = distributable;
            for (Object cel : currentFlex) {
                Layoutable1D cell = (Layoutable1D) cel;
                int oldSize = cell.size;
                int newSize = oldSize + distributable * cell.weight / currentWeights;
                if (newSize < cell.maxSize) {
                    totalWeights += cell.weight;
                    flex.add((Object)cell);
                } else {
                    newSize = cell.maxSize;
                }
                cell.size = newSize;
                currentDistributable -= newSize - oldSize;
            }
            if (distributable <= currentDistributable) break;
            distributable = currentDistributable;
        }
        List cellsByWeight = (List)flex.stream().sorted(Comparator.comparing(Layoutable1D::getWeight).reversed()).collect(Collectors.toList());
        for (Object cel : cellsByWeight) {
            if (distributable <= 0) continue;
            Layoutable1D cell = (Layoutable1D) cel;
            ++cell.size;
            --distributable;
        }
        return available - distributable;
    }
}