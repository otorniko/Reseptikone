package dev.otorniko;
public class CheckableNodeData {
    private String text;
    private boolean checked;

    public CheckableNodeData(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return text;
    }
}