package com.fintrust.chart.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.XAxis;
import org.zkoss.chart.plotOptions.AreaPlotOptions;
import org.zkoss.chart.LinearGradient;
import org.zkoss.chart.Stop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import com.fintrust.model.DailyUserActiveTimeData;

public class DailyUserActiveTimeChartComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Charts activeUserChart;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        activeUserChart.getTitle().setText(" ");
        activeUserChart.getSubtitle().setText("Hourly active users across the platform");
        activeUserChart.getTitle().setAlign("left");
        activeUserChart.getSubtitle().setAlign("left");

        // X-Axis: Hour of Day
        XAxis xAxis = activeUserChart.getXAxis();
        xAxis.setTitle("Hour of Day");
        xAxis.setTickInterval(1);

        activeUserChart.getYAxis().setTitle("Active Users");

        // Area styling (banking-friendly)
        AreaPlotOptions plotOptions = activeUserChart.getPlotOptions().getArea();
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1);
        gradient.setStops(
            new Stop(0, "rgb(46,125,50)"),
            new Stop(0.7, "rgba(46,125,50,0)")
        );

        plotOptions.setColor(gradient);
        plotOptions.setLineWidth(2);
        plotOptions.getMarker().setRadius(3);
        plotOptions.setShadow(false);

        // Series
        Series series = activeUserChart.getSeries();
        series.setType("area");
        series.setName("Active Users");
        series.setData(
            Arrays.stream(DailyUserActiveTimeData.getData())
                  .map(d -> new Point(d[0], d[1]))
                  .collect(Collectors.toList())
                  .toArray(new Point[0])
        );
    }
}

