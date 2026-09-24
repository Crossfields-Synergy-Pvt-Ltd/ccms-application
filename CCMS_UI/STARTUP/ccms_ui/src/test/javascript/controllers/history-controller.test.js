describe('historyControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, historyFactory;
    var mockConfig, mockInform;

    beforeEach(module('inform'));
    beforeEach(module('historyControllers'));
    beforeEach(module('historyFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _historyFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $controller = _$controller_;
        historyFactory = _historyFactory_;
        mockInform = { add: jasmine.createSpy('inform.add') };
        mockConfig = { districts: [{ state: 'Guntur-17', code: 'Guntur-17' }] };
        $rootScope.privilege = {district: 'ALL', mandal: 'ALL', gp: 'ALL'};
        $httpBackend.whenGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL&village=ALL')
            .respond([{name: 'DCU-001', gateway_identifier: 'DCU001'}]);
    }));

    afterEach(function() {
        try { $httpBackend.verifyNoOutstandingExpectation(); } catch (e) {}
        try { $httpBackend.verifyNoOutstandingRequest(); } catch (e) {}
    });

    function createController() {
        $controller('historyListControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            historyFactory: historyFactory,
            config: mockConfig,
            inform: mockInform
        });
    }

    it('loads DCU names and initializes pagination', function() {
        createController();
        $httpBackend.flush();
        expect($scope.dcu_data.length).toBe(1);
        expect($scope.itemsPerPage).toBe(25);
        expect($scope.figureOutTodosToDisplay).toBeDefined();
    });

    it('does not request history without a selected DCU', function() {
        createController();
        $httpBackend.flush();
        $scope.showdate();
        expect(mockInform.add).toHaveBeenCalled();
    });

    it('loads and paginates history data', function() {
        createController();
        $httpBackend.flush();
        $scope.selected_dcu.name = {gateway_identifier: 'DCU001'};
        $httpBackend.expectGET('/meter/meter_data_between_date?id=DCU001&start_date=&end_date=')
            .respond([{dcu_name: 'DCU', kwh_total: '1', consumption: '2'}]);
        $scope.showdate();
        $httpBackend.flush();
        expect($scope.todos.length).toBe(1);
        expect($scope.list.length).toBe(1);
        expect($scope.loading).toBe(false);
    });

    it('keeps existing table data when exporting', function() {
        createController();
        $httpBackend.flush();
        $scope.selected_dcu.name = {gateway_identifier: 'DCU001'};
        $scope.list = [{dcu_name: 'existing'}];
        $httpBackend.expectGET('/meter/export_history?id=DCU001&start_date=' +
            encodeURIComponent(moment($scope.datePicker.date.startDate).format('DD/MM/YYYY')) +
            '&end_date=' + encodeURIComponent(moment($scope.datePicker.date.endDate).format('DD/MM/YYYY')))
            .respond(200, 'csv', {'x-filename': 'history.csv', 'content-type': 'text/csv'});
        $scope.export_history();
        $httpBackend.flush();
        expect($scope.list[0].dcu_name).toBe('existing');
    });
});
