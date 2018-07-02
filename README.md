# AndroidMvpPlugin
It's a plugin to generate code for mvp.

The plugin support both activity and fragment.  

**Genarate activity:**  
It will generate activity,contract,presenter,module(for dependency).
The activity and module will be injected in the `ActivityBindingModule.class`  
when you choose create layout,the layout will be added in the `AndroidManifest.xml`.  

**Genarate fragment:**  
It will generate fragment,contract and presenter.  
The presenter will be injected in the module of the activity it hold.  

### ActivityBindingModule:  
The class to hold the injection for all view component.
Every activity to the dagger inject system will be added in this class.  



