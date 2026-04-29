public enum Finder {
    LINE_BY_LINE,
    CHAR_BY_CHAR,
    REGEX
}
public enum InMemory {
    NONE,
    LIST_BY_FILE,
    LIST_BY_LINE
}
public enum DatasetType{
    G,
    P
}

public interface Dataset {
    String folder;
    String fileStructName;
    int numOfLines;

    public Result FindName(string name);
}

public Result {
    boolean isSuccess;
    int file;
    int line; 
}



public map<Dataset,int> file = [{Dataset.G,10000}]


