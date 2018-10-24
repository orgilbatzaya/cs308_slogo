package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * This class stores a list of items as well as the display of that list.
 * This is handy for displaying past commands the user has typed, current variables in the environment, and current user-defined commands in the environment.
 * There is also a maximum size that the list can be before the oldest elements of the list start disappearing. When the maximum size is -1, the maximum size of the list is infinite.
 * @author Austin Kao
 */
public class CurrentEnvironmentDisplay {
    private VBox parentBox;
    private ListView<String> currentDisplay;
    private ObservableList<String> currentItems;
    private int maxSize;

    public CurrentEnvironmentDisplay(double height, String label) {
        this(height, -1, label);
    }

    public CurrentEnvironmentDisplay(double height, int max, String label) {
        currentDisplay = new ListView<>();
        Label displayLabel = new Label(label);
        currentItems = FXCollections.observableArrayList();
        currentDisplay.setMaxHeight(height);
        maxSize = max;
        parentBox = new VBox(displayLabel, currentDisplay);
        //currentDisplay.setOnMouseClicked(e -> editItem(currentDisplay.getEditingIndex()));
    }

    public void addItem(String newItem) {
        if(!currentItems.contains(newItem)) {
            currentItems.add(0, newItem);
        }
        if(maxSize >= 0 && currentItems.size() > maxSize) {
            currentItems.remove(maxSize - 1);
        }
        currentDisplay.setItems(currentItems);
    }

    public void removeItem(String item) {
        if(currentItems.contains(item)) {
            currentItems.remove(item);
        }
        currentDisplay.setItems(currentItems);
    }

    public void clear() {
        currentItems.clear();
        currentDisplay.setItems(currentItems);
    }

    public VBox getDisplay() {
        return parentBox;
    }

    public void editItem(int index) {

    }
}
