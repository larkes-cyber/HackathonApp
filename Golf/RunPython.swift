//
//  RunPython.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 26.08.2023.
//

import Foundation
import PythonKit

func runPython() -> PythonObject {
    let sys = Python.import("sys")
    sys.path.append("Users/alexanderskorokhodov/dev/Golf/Golf/PythonScipts")
    let file = Python.import("py-file")
    
    let response = file.hello()
    
    return response
}
