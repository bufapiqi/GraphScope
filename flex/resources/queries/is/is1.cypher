MATCH (p: PERSON { id: $personId}) - [:ISLOCATEDIN] ->(c : place) return p.firstName AS friendFirstName, p.lastName as friendLastName, p.birthday as personBirthday, p.locationIP as personLocationIP, p.browserUsed as personBrowserUsed, c.id AS cityID, p.gender as friendGender, p.creationDate AS personCreationDate 