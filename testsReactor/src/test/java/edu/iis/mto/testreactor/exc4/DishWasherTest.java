package edu.iis.mto.testreactor.exc4;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DishWasherTest {

    @Mock
    WaterPump waterPump;
    @Mock
    Engine engine;
    @Mock
    DirtFilter dirtFilter;
    @Mock
    Door door;


    private DishWasher washer;
    private ProgramConfiguration defaultProgram;

    @Before
    public void setup() {
        washer = new DishWasher(waterPump, engine, dirtFilter, door);
        defaultProgram = ProgramConfiguration.builder().withProgram(WashingProgram.ECO).withTabletsUsed(true).build();
    }


    @Test
    public void washerStartAttemptWithClosedDoorShouldResultInError() {
        when(door.closed()).thenReturn(true);

        RunResult result = washer.start(defaultProgram);

        Assert.assertThat(result.getStatus(), is(Status.DOOR_OPEN_ERROR));
    }
    @Test
    public void washerStartAttemtWithDirtFilterShouldResultInError() {
        when(dirtFilter.capacity()).thenReturn(30.0);
        when(door.closed()).thenReturn(false);

        RunResult result = washer.start(defaultProgram);

        Assert.assertThat(result.getStatus(), is(Status.ERROR_FILTER));
    }

    @Test
    public void washerRunWithEcoProgramAndTabletsAndWorkingElementsShouldSucceed() {
        when(dirtFilter.capacity()).thenReturn(60.0);
        when(door.closed()).thenReturn(false);

        RunResult result = washer.start(defaultProgram);

        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

//    @Test
//    public void washerStartAttemtWithDirtPumPShouldResultInError()  {
//        when(dirtFilter.capacity()).thenReturn(60.0);
//        when(door.closed()).thenReturn(false);
//
//        RunResult result = washer.start(defaultProgram);
//
//        Assert.assertThat(result.getStatus(), is(Status.ERROR_PUMP));
//    }
    @Test
    public void washerShouldUseWaterPumpDrainTwiceWhileLaunchedWithNotRinseProgram() throws PumpException {
        when(dirtFilter.capacity()).thenReturn(60.0);
        when(door.closed()).thenReturn(false);

        RunResult result = washer.start(defaultProgram);

        Mockito.verify(waterPump, times(2)).drain();
    }
    @Test
    public void washerShouldUseEngineOnlyOnceWhenLaunchedWithRinseProgram() throws EngineException {
        when(dirtFilter.capacity()).thenReturn(60.0);
        when(door.closed()).thenReturn(false);

        ProgramConfiguration rinseProgram = ProgramConfiguration.builder().withProgram(WashingProgram.RINSE).withTabletsUsed(true).build();

        RunResult result = washer.start(rinseProgram);

        Mockito.verify(engine).runProgram(rinseProgram.getProgram().getTimeInMinutes());

    }



}
