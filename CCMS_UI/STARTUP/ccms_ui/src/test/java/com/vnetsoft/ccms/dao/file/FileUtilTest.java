package com.vnetsoft.ccms.dao.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.junit.Test;

import com.vnetsoft.ccms.pojo.ui.MeterDataUI;

public class FileUtilTest {

    @Test
    public void convertsHourMinuteDurationsToSortableMinutes() {
        assertEquals(125, FileUtil.durationToMinutes("2:05"));
        assertEquals(600, FileUtil.durationToMinutes("10:00"));
    }

    @Test
    public void invalidDurationsSortAtZero() {
        assertEquals(0, FileUtil.durationToMinutes("invalid"));
    }

    @Test
    public void buildsEveryDateInRequestedRangeWithoutThirtyDayCap() {
        List<String> files = FileUtil.getDataFilenamesBetweenDate("DCU1", "01/01/2024", "31/01/2024", "meter_data.csv");
        assertEquals(31, files.size());
        assertTrue(files.get(0).endsWith("/2024/01/DCU1/01/meter_data.csv"));
        assertTrue(files.get(30).endsWith("/2024/01/DCU1/31/meter_data.csv"));
    }

    @Test
    public void reversedDateRangeReturnsNoFiles() {
        assertTrue(FileUtil.getDataFilenamesBetweenDate("DCU1", "02/01/2024", "01/01/2024", "meter_data.csv").isEmpty());
    }

    @Test
    public void mapsMeterCsvColumnsToDistinctUiFields() throws Exception {
        File file = File.createTempFile("history-meter", ".csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("DCU-ID,DCU Name,01 Jan 2024 12:00,,,,230,5,1234,0.98,5678,0,0,0,0,0\n");
        }
        List<MeterDataUI> data = FileUtil.getMeterData(java.util.Collections.singletonList(file.getAbsolutePath()));
        assertEquals(1, data.size());
        assertEquals("DCU-ID", data.get(0).getDcu_id());
        assertEquals("DCU Name", data.get(0).getDcu_name());
        assertEquals("1234", data.get(0).getKwh_total());
        assertEquals("5678", data.get(0).getConsumption());
        assertEquals("230", data.get(0).getR_phase_voltage());
        file.delete();
    }
}
