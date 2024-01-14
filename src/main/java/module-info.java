module JavaAgent {
    opens dev.padak;
    requires org.slf4j;
    requires org.apache.bcel;
    requires java.base;
    requires java.instrument;
    exports dev.padak;
}