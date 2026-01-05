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

import com.fintrust.model.UserRegistrationData;

public class UserRegistrationChartComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Charts userChart;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        userChart.getTitle().setText("");
        userChart.getSubtitle().setText("New users registered per week");
        userChart.getTitle().setAlign("left");
        userChart.getSubtitle().setAlign("left");

        XAxis xAxis = userChart.getXAxis();
        xAxis.setType("datetime");

        userChart.getYAxis().setTitle("Number of Users");

        AreaPlotOptions plotOptions = userChart.getPlotOptions().getArea();
        LinearGradient fillColor = new LinearGradient(0, 0, 0, 1);
        fillColor.setStops(new Stop(0, "rgb(102, 204, 255)"), new Stop(0.7, "rgba(102, 204, 255,0)"));
        plotOptions.setColor(fillColor);
        plotOptions.getMarker().setRadius(3);
        plotOptions.setLineWidth(2);

        Series series = userChart.getSeries();
        series.setType("area");
        series.setName("New Users");
        series.setData(Arrays.stream(UserRegistrationData.getData())
                .map(numbers -> new Point(numbers[0], numbers[1]))
                .collect(Collectors.toList())
                .toArray(new Point[0]));
    }
}

