package com.treasure_data.bulk_import.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.treasure_data.bulk_import.prepare_parts.PreparePartsException;

public class TestIntColumnValue extends ColumnValueTestUtil<Integer> {

    @Before
    public void createResources() throws Exception {
        super.createResources();
        columnValue = new IntColumnValue(ColumnType.INT);

    }

    @After
    public void destroyResources() throws Exception {
        super.destroyResources();
    }

    @Override
    public void createExpecteds() {
        expecteds.add(Integer.MAX_VALUE);
        expecteds.add(Integer.MIN_VALUE);

        int numExec = rand.nextInt(10000);
        for (int i = 0; i < numExec; i++) {
            expecteds.add(rand.nextInt());
        }
    }

    @Test
    public void returnNormalValues() throws Exception {
        for (int i = 0; i < expecteds.size(); i++) {
            columnValue.parse("" + expecteds.get(i));
            assertColumnValueEquals(expecteds.get(i),
                    (IntColumnValue) columnValue);
        }
    }

    void assertColumnValueEquals(int expected, IntColumnValue actual) {
        Assert.assertEquals(expected, actual.getInt());
    }

    @Override
    public void assertWrittenValueEquals(int index) {
        Assert.assertEquals(expecteds.get(index), (Integer) writer.getRow().get(KEY));
    }

    @Override
    public void prepareMockForWriting() throws Exception {
        writer = spy(writer);
        doThrow(new PreparePartsException("")).when(writer).write(any(int.class));
    }
}
