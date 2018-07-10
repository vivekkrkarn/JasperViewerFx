JasperViewerFx
------------------

![demo](https://raw.githubusercontent.com/vivekkrkarn/JasperViewerFx/master/demo.gif)

JasperViewrFx is a full featured Jasper report viewer written completely in JavaFx. 
It has the following features:
* View and zoom  all of the pages of a Jasper report document.
* Export report documents to the following formats: PDF, PNG, DOCX, XLSX, HTML
* Print reports using the system dialog.
 
Originally this project has been forked from [mgrecol/JasperViewerFx](https://github.com/mgrecol/JasperViewerFx) and I decided to improve it by code cleaning, refactoring, simplifying and small GUI redesigning.

How to use:
------------
JasperViewerFx is made to be included in a running JavaFxApplication. This means that a running JavaFx application would already have have a stage created. What you need to integrate this feature into your project is just put jar into your classpath, after that:
```java
JRViewer jrViewer = new JRViewer();
Stage viewerStage = jrViewer.getViewerStage(jasperPrint);
viewerStage.show();
```
Additionally you can pass supported language parameter, scene size and export filetypes which you need:
```java
JRViewer jrViewer = new JRViewer(
    JRViewerSupportedLocale.EN,
    640, 420, "./reports", "my report.pdf",
    Arrays.asList(
        JRViewerFileExportExtention.PDF,
        JRViewerFileExportExtention.DOCX
    )
);
```

You can listen to fail to load report event:
```java
final Stage viewerStage = jrViewer.buildStage(jasperPrint);
viewerStage.addEventHandler(com.mgrecol.jasper.jasperviewerfx.event.JR_REPORT_LOAD_FAILED, e -> {
    System.out.println("failed to load report");
});
```

To build jar with dependencies use maven command:
```
assembly:assembly -DdescriptorId=jar-with-dependencies
```
