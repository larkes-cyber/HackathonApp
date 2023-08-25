//
//  GolfApp.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 25.08.2023.
//

import SwiftUI
import SwiftData

@main
struct GolfApp: App {
    var sharedModelContainer: ModelContainer = {
        let schema = Schema([
            Item.self,
        ])
        let modelConfiguration = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)

        do {
            return try ModelContainer(for: schema, configurations: [modelConfiguration])
        } catch {
            fatalError("Could not create ModelContainer: \(error)")
        }
    }()

    var body: some Scene {
        WindowGroup {
            CameraView()
        }
        .modelContainer(sharedModelContainer)
    }
}
