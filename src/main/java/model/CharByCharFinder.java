package model;

public final class CharByCharFinder implements Finder {
    @Override
    public boolean matches(String text, String target) {
        if (text == null || target == null) {
            return false;
        }

        if (target.isEmpty()) {
            return true;
        }

        if (target.length() > text.length()) {
            return false;
        }

        for (int i = 0; i <= text.length() - target.length(); i++) {
            int j = 0;
            while (j < target.length() && text.charAt(i + j) == target.charAt(j)) {
                j++;
            }
            if (j == target.length()) {
                return true;
            }
        }

        return false;
    }
}
