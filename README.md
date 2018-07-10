JasperViewerFx
------------------

![demo](https://raw.githubusercontent.com/vivekkrkarn/JasperViewerFx/master/demo.gif)



JasperViewrFx is a full featured Jasper report viewer written completely in JavaFx.

It has the following features:

* View and zoom  all of the pages of a Jasper report document.
* Export report documents to the following formats: PDF, PNG, DOCX, XLSX, HTML
* Print reports using the system dialog.
 
Originally this project has been developed by [mgrecol/JasperViewerFx](https://github.com/mgrecol/JasperViewerFx) then improved by [AlexJudge/JasperViewerFx](https://github.com/AlexJudge/JasperViewerFx) and I was in search for a Jasper Reports Viewer For JavaFx, found it, decided to further improve it for my needs, used material design components from [jfoenixadmin / JFoenix](https://github.com/jfoenixadmin/JFoenix) .


How to use:
------------
JasperViewerFx is made to be included in a running JavaFxApplication. This means that a running JavaFx application would already have have a stage created. What you need to integrate this feature into your project is just put jar into your classpath, after that:

```java
JRViewer jrViewer = new JRViewer();
Stage viewerStage = jrViewer.getViewerStage(jasperPrint);
viewerStage.show();
```



If you want to add css styles for JasperViewerFx then paste this css snippet in your stylesheet :

```css

.border-pane {

    -fx-background-color: #ffffff;


}


.jfx-slider {

    -fx-font-size: 12px;

}


.jfx-combo-box {
    -fx-font-size: 14px;
    -fx-text-fill: #2196F3;
    -fx-font-weight: bold;

    -jfx-focus-color: #2196F3;
    -jfx-label-float: true;
}


.jfx-button {
    -fx-font-size: 14px;
    -fx-text-fill: #ffffff;
    -fx-border-color: #2196F3;
    -fx-background-color: #2196F3;

    -jfx-button-type: RAISED;
}




#toolbar_Hbox {

    -fx-background-color: #ffffff;
}



#imageHolder {

    -fx-background-color: #2196F3;

}
```


then add that stylesheet in your viewerStage like

```java

viewerStage.getScene().getStylesheets().add("/app/css/fx_report_viewer/fx_report_viewer.css");

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



To build jar with dependencies use maven command:

```
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```
