//
//  Item.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 25.08.2023.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
