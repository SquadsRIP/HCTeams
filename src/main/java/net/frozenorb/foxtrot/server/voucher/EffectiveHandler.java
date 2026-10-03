package net.frozenorb.foxtrot.server.voucher;

import java.util.ArrayList;
import java.util.List;

public class EffectiveHandler {
   private List<Integer> effectivePackages = new ArrayList<>();

   public EffectiveHandler() {
      this.loadPackages();
   }

   public void loadPackages() {
      this.effectivePackages.add(2995317);
      this.effectivePackages.add(2995325);
      this.effectivePackages.add(2995326);
      this.effectivePackages.add(2995327);
      this.effectivePackages.add(2995333);
      this.effectivePackages.add(2995343);
      this.effectivePackages.add(3095615);
      this.effectivePackages.add(3263578);
      this.effectivePackages.add(4152653);
      this.effectivePackages.add(2995344);
      this.effectivePackages.add(2995350);
      this.effectivePackages.add(2995354);
      this.effectivePackages.add(2995356);
      this.effectivePackages.add(2995358);
      this.effectivePackages.add(3110697);
      this.effectivePackages.add(3284922);
      this.effectivePackages.add(4156976);
      this.effectivePackages.add(3180037);
      this.effectivePackages.add(5224213);
      this.effectivePackages.add(5224218);
      this.effectivePackages.add(4753839);
      this.effectivePackages.add(3242328);
      this.effectivePackages.add(3351528);
      this.effectivePackages.add(4356882);
      this.effectivePackages.add(4841877);
      this.effectivePackages.add(4557465);
      this.effectivePackages.add(4557467);
      this.effectivePackages.add(4244976);
      this.effectivePackages.add(4296329);
      this.effectivePackages.add(4545681);
      this.effectivePackages.add(4887221);
      this.effectivePackages.add(4730630);
      this.effectivePackages.add(4741224);
      this.effectivePackages.add(4643515);
      this.effectivePackages.add(4592932);
   }

   public List<Integer> getEffectivePackages() {
      return this.effectivePackages;
   }
}
