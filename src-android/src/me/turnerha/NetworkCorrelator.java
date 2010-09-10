package me.turnerha;

/**
 * 
 * This is pretty interesting. We should act differently with the GPS versus the
 * Network when interacting with the phone state listener. The GPS, once someone
 * is using it - actually changes state relatively frequently and we can rely on
 * the lastKnownLocation call on the gps provider to give us decent enough data
 * to correlate the GPS and the SignalStrength. However, the Network location
 * provider updates very infrequently, so we don't want to leave the
 * PhoneStateListener turned on or we will kill the phone's resources polling
 * the Phone state when then
 * 
 * 
 * @author hamiltont
 * 
 */
public class NetworkCorrelator {

}
