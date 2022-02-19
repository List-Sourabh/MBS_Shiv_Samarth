-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneyCombMR2

-keepclasseswithmembernames class * {
  native <methods>;
}

-keepclasseswithmembers class * {
  public <init> (andriod.content.Context, andriod.util.AttributeSet);
}