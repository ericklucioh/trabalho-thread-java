package model;

public interface Dataset {
    String folder();

    String fileStructName();

    int numOfLines();

    Result findName(String name);
}
