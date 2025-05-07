package dev.otorniko;

/**
 * Raaka-aine listan raaka-aineiden tietojen esittämiseen. Luokka sisältää
 * raaka-aineen nimen ja sen valintatilan. Luokassa on myös getterit ja setterit
 * raaka-aineen nimen ja valintatilan käsittelyyn.
 */
public class CheckableNodeData {

    private String name;
    private boolean checked;

    public CheckableNodeData(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() { return name; }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) { this.checked = checked; }
}