package com.fintrust.chart.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvents;
import org.zkoss.chart.LinearGradient;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.Stop;
import org.zkoss.chart.XAxis;
import org.zkoss.chart.plotOptions.AreaPlotOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import com.fintrust.model.LineTimeSeriesData;

public class LineTimeSeriesComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = -5484435218366771478L;

    @Wire
    Charts chart;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Titles
        chart.getTitle().setText("");
        chart.getTitle().setAlign("left");
        chart.getSubtitle().setAlign("left");

        // X-axis as datetime
        XAxis xAxis = chart.getXAxis();
        xAxis.setType("datetime");

        // Y-axis title
        chart.getYAxis().setTitle("Transaction Amount (INR)");

        // Disable legend
        chart.getLegend().setEnabled(false);

        // Area plot options
        AreaPlotOptions plotOptions = chart.getPlotOptions().getArea();
        LinearGradient fillColor = new LinearGradient(0, 0, 0, 1);
        fillColor.setStops(
            new Stop(0, "rgb(76, 175, 254)"), 
            new Stop(0.7, "rgba(76, 175, 254, 0)")
        );
        plotOptions.setColor(fillColor);
        plotOptions.getMarker().setRadius(2);
        plotOptions.setLineWidth(1);
        plotOptions.setShadow(false);
        plotOptions.getStates().getHover().setLineWidth(1);
        plotOptions.setThreshold(null);

        // Series data
        Series series = chart.getSeries();
        series.setType("area");
        series.setName("Daily Transactions");
        series.setData(Arrays.stream(LineTimeSeriesData.getData())
                .map(numbers -> new Point(numbers[0], numbers[1]))
                .collect(Collectors.toList())
                .toArray(new Point[0]));

        // Optional: change gradient on theme change
        chart.addEventListener(0, ChartsEvents.ON_PLOT_THEME_CHANGE, new EventListener() {
            public void onEvent(Event event) throws Exception {
                LinearGradient newFill = new LinearGradient(0, 0, 0, 1);
                newFill.setStops(chart.getColors().get(0).stringValue(), "rgba(44,175,254,0)");
                chart.getPlotOptions().getArea().setFillColor(newFill);
            }
        });
    }
}
